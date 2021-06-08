package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import common.*;
import common.commands.*;
import common.exceptions.InsufficientPermissionException;
import common.model.Worker;

public class ServerRequestHandler {
    private static final int TOKEN_LIST_MAX_SIZE = 100;
    private CollectionManager manager;
    private DatabaseHandler dbHandler;
    private List<String> tokenList = new LinkedList<>();
    private AuthManager authManager = new AuthManager();
    //private ExecutorService forkJoinPool = ForkJoinPool.commonPool(); ////TODO: not my thread
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
    private volatile long nextWorkerId;

    public ServerRequestHandler(CollectionManager manager) {
        this.manager = manager;
    }

    public CollectionManager getManager() {
        return manager;
    }
//
//
//    public Response processClientRequest(Request r) {
//        AbstractCommand command = r.getCommand();
//        System.out.println("Обрабатываю команду " + command.getCommandType().toString());
//        return executeRequest(command);
//    }
public ServerRequestHandler(CollectionManager manager, DatabaseHandler dbHandler) {
    this.manager = manager;
    this.dbHandler = dbHandler;
    this.nextWorkerId = dbHandler.getNextWorkerId();
}


    public Response processClientRequest(Request r) {
        RequestExecutor executor = new RequestExecutor(r);
       // Future<Response> responseFuture = forkJoinPool.submit(executor); //  TODO
        Future<Response> responseFuture = fixedThreadPool.submit(executor);
        while (true) {
            if (responseFuture.isDone()) {
                try {
                    return responseFuture.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
             Thread.yield();
        }
    }

    private Response executeRequest(AbstractCommand command, String initiator) {
        try {
            CommandType type = command.getCommandType();


            switch (type) {
                case ADD:
                    AddCommand addCommand = (AddCommand) command;
                    Worker t = addCommand.getWorker();
                    t.setId(++nextWorkerId);
                    Worker.addToIdMap(t);
                    if(dbHandler.insertWorker(t, initiator)) return manager.add(t, addCommand.ifMax(), addCommand.ifMin()); //TODO: add_if_min add_if_max ?
                    return new Response("Ошибка при добавлении объекта в базу данных.");
                case CLEAR:
                    return manager.clear(initiator);
                case COUNT_LESS_THAN_END_DATE:
                    CountLessThanEndDateCommand countLessThanEndDate = (CountLessThanEndDateCommand) command;
                    System.out.println(countLessThanEndDate.getEndDate());
                    return manager.countLessThanEndDate(countLessThanEndDate.getEndDate());
                //case EXECUTE_SCRIPT:
                  //  ExecuteScriptCommand executeScriptCommand = (ExecuteScriptCommand) command;
                   // return;
                   //
                case SHOW:
                    ShowCommand showCommand = (ShowCommand) command;
                    return manager.showCollection();
                case HELP:
                    HelpCommand helpCommand = (HelpCommand) command;
                    return manager.help();
                case REMOVE_LOWER:
                    RemoveLowerCommand removeLowerCommand = (RemoveLowerCommand) command;
                    return manager.removeLower(removeLowerCommand.getWorker(), initiator);
                case REMOVE_BY_ID:
                    RemoveByIdCommand removeByIdCommand = (RemoveByIdCommand) command;
                    //return manager.removeById(removeByIdCommand.getId());
                    long idToRemove = removeByIdCommand.getId();
                    try {
                        if (dbHandler.removeWorkerByID(idToRemove, initiator)) return manager.removeById(idToRemove);
                        else return new Response("Элемент с указанным id не существует.");
                    } catch (SQLException e) {
                        System.out.println("HERE!!!");
                        e.printStackTrace();
                        dbHandler.rollback();
                        return new Response("Ошибка выполнения запроса", false);
                    } catch (InsufficientPermissionException e) {
                        return new Response("Запрос отклонен: недостаточно прав.");
                    }
                case UPDATE_ID:
                    UpdateIdCommand updateIdCommand = (UpdateIdCommand) command;
//                    Worker oldT = manager.getWorkerById(updateIdCommand.getId());
//                    Worker newT = updateIdCommand.getNewWorker();
//                    if (oldT == null) return new Response("Элемент с указанным id не найден.");
//                    if (updateIdCommand.isUpdateName()) oldT.setName(newT.getName());
//                    if (updateIdCommand.isUpdateSalary()) oldT.setSalary(newT.getSalary());
//                    if (updateIdCommand.isUpdateCoordinates()) oldT.setCoordinates(newT.getCoordinates());
//                    if (updateIdCommand.isUpdateStartDate()) oldT.setStartDate(newT.getStartDate());
//                    if (updateIdCommand.isUpdateEndDate()) oldT.setEndDate(newT.getEndDate());
//                    if (updateIdCommand.isUpdateStatus()) oldT.setStatus(newT.getStatus());
//                    if (updateIdCommand.isUpdatePerson()) oldT.setPerson(newT.getPerson());
//                    return manager.updateId(updateIdCommand.getId(), oldT);
                    long id = updateIdCommand.getId();
                    Worker oldT = manager.getWorkerById(updateIdCommand.getId());
                    if (oldT == null) return new Response("Элемент с указанным id не найден.");
                    if(!dbHandler.isOwnerOf(updateIdCommand.getId(), initiator)) return new Response("Недостаточно прав.");
                    Worker newT = updateIdCommand.getNewWorker();
                    newT.setId(updateIdCommand.getId());
                    if (updateIdCommand.isUpdateName()) {
                        dbHandler.updateWorkerName(newT.getName(), id);
                        oldT.setName(newT.getName());
                    }
                    if (updateIdCommand.isUpdateCoordinates()) {
                        dbHandler.updateWorkerCoordinates(newT.getCoordinates(), id);
                        oldT.setCoordinates(newT.getCoordinates());
                    }
                    if (updateIdCommand.isUpdateSalary()) {
                        dbHandler.updateWorkerSalary(newT.getSalary(), id);
                        oldT.setCoordinates(newT.getCoordinates());
                    }
                    if (updateIdCommand.isUpdateStartDate()) {
                        dbHandler.updateWorkerStartDate(newT.getStartDate(), id);
                        oldT.setStartDate(newT.getStartDate());
                    }
                    if (updateIdCommand.isUpdateEndDate()) {
                        dbHandler.updateWorkerEndDate(newT.getEndDate(), id);
                        oldT.setEndDate(newT.getEndDate());
                    }
                    if (updateIdCommand.isUpdateStatus()) {
                        dbHandler.updateWorkerStatus(newT.getStatus(), id);
                        oldT.setStatus(newT.getStatus());
                    }
                    return manager.updateId(updateIdCommand.getId(), oldT);
                case SUM_OF_SALARY:
                    SumOfSalaryCommand sumOfSalaryCommand = (SumOfSalaryCommand) command;
                    return manager.sumOfSalary();
                case INFO:
                    InfoCommand infoCommand = (InfoCommand) command;
                    return manager.getInfo();
                case PRINT_FIELD_ASCENDING_PERSON:
                    PrintFieldAscendingPersonCommand printFieldAscendingPersonCommand = (PrintFieldAscendingPersonCommand) command;
                    return manager.printPerson();
                default:
                    break;
            }
        } catch (NullPointerException e) {
            System.out.println("Получен неверный запрос от клиента.");
        }catch (SQLException e) {
            System.out.println("Непредвиденная ошибка SQL.");
            e.printStackTrace();
        }
        return null;
    }
    private class AuthManager {
        public Response handleAuth(AbstractCommand command) {
            AuthCommand authCommand = (AuthCommand) command;
            try {
                String username = authCommand.getUsername();
                String password = authCommand.getPassword();
                if (authCommand.getAuthType().equals(AuthCommand.AuthType.REGISTER)) {
                    if(dbHandler.registerUser(username, password))
                        return new Response("Пользователь " + username + " успешно зарегистрирован", true);
                    else
                        return new Response("Пользователь с таким именем уже существует.", false);
                }
                if (authCommand.getAuthType().equals(AuthCommand.AuthType.LOGIN)) {
                    boolean isUserFound = dbHandler.validateUser(username, password);
                    if (isUserFound) return new Response("Авторизация успешна.", true, generateServerToken());
                    else return new Response("Ошибка авторизации. Проверьте правильность данных", false);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return new Response("Ошибка исполнения запроса. Повторите попытку позже.", false);
            }
            return null;
        }

        private String generateServerToken() {
            String token = Long.toHexString(Double.doubleToLongBits(Math.random()));
            tokenList.add(token);
            if (tokenList.size() > TOKEN_LIST_MAX_SIZE ) tokenList.remove(1);
            return token;
        }
    }

    private class RequestExecutor implements Callable<Response> {

        private Request r;

        public RequestExecutor(Request r) {
            this.r = r;
        }
        @Override
        public Response call() throws Exception {
            AbstractCommand command = r.getCommand();
            String initiator = r.getInitiator();
            System.out.println("Обрабатываю команду " + command.getCommandType().toString());
            if (command.getCommandType().equals(CommandType.AUTH)) return authManager.handleAuth(command);
            else if(tokenList.contains(r.getToken())) return executeRequest(command, initiator);
            else return new Response("Отказ в обработке: требуется авторизация");
        }
    }
}
