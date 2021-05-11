package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import common.*;
import common.commands.*;
import common.model.Worker;

public class ServerRequestHandler {

    private ObjectInputStream clientReader;
    private ObjectOutputStream clientSender;
    private CollectionManager manager;

    public ServerRequestHandler(CollectionManager manager) {
        this.manager = manager;
    }

    public ObjectInputStream getClientReader() {
        return clientReader;
    }

    public void setClientReader(ObjectInputStream clientReader) {
        this.clientReader = clientReader;
    }

    public ObjectOutputStream getClientSender() {
        return clientSender;
    }

    public void setClientSender(ObjectOutputStream clientSender) {
        this.clientSender = clientSender;
    }

    public Response processClientRequest(Request r) {
        AbstractCommand command = r.getCommand();
        System.out.println("Обрабатываю команду " + command.getCommandType().toString());
        return executeRequest(command);
    }

    private Response executeRequest(AbstractCommand command) {
        try {
            CommandType type = command.getCommandType();


            switch (type) {
                case ADD:
                    AddCommand addCommand = (AddCommand) command;
                    Worker t = addCommand.getWorker();
                    t.setId(manager.generateId());
                    return manager.add(t, addCommand.ifMax(), addCommand.ifMin());
                case CLEAR:
                    return manager.clear();
                case COUNT_LESS_THAN_END_DATE:
                    CountLessThanEndDateCommand countLessThanEndDate = (CountLessThanEndDateCommand) command;
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
                    return manager.removeLower(removeLowerCommand.getWorker());
                case REMOVE_BY_ID:
                    RemoveByIdCommand removeByIdCommand = (RemoveByIdCommand) command;
                    return manager.removeById(removeByIdCommand.getId());
                case UPDATE_ID:
                    UpdateIdCommand updateIdCommand = (UpdateIdCommand) command;
                    Worker oldT = manager.getWorkerById(updateIdCommand.getId());
                    Worker newT = updateIdCommand.getNewWorker();
                    if (oldT == null) return new Response("Элемент с указанным id не найден.");
                    if (updateIdCommand.isUpdateName()) oldT.setName(newT.getName());
                    if (updateIdCommand.isUpdateSalary()) oldT.setSalary(newT.getSalary());
                    if (updateIdCommand.isUpdateCoordinates()) oldT.setCoordinates(newT.getCoordinates());
                    if (updateIdCommand.isUpdateStartDate()) oldT.setStartDate(newT.getStartDate());
                    if (updateIdCommand.isUpdateEndDate()) oldT.setEndDate(newT.getEndDate());
                    if (updateIdCommand.isUpdateStatus()) oldT.setStatus(newT.getStatus());
                    if (updateIdCommand.isUpdatePerson()) oldT.setPerson(newT.getPerson());
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
        }
        return null;
    }

}
