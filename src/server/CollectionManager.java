package server;
import common.*;
import common.exceptions.InsufficientPermissionException;
import common.model.*;
import java.util.*;


import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CollectionManager {
    private long nextId;
    private LinkedHashSet<Worker> collectionSet;
    private Set<Worker> collection = new TreeSet<>();
    private static HashMap<String, String> description;
    private String listType;
    private final Date initDate;
    private FileManager fileManager;
    static public String path;

    //private ReadWriteLock collectionLocker;
    private DatabaseHandler dbHandler;

    public CollectionManager(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    {

        collectionSet = new LinkedHashSet<Worker>();
        initDate = new Date();
        description = new HashMap<String, String>();
        listType = collectionSet.getClass().getSimpleName();
        //collectionLocker = new ReentrantReadWriteLock();
        //File file = new File("");
        //String path = "" + file.getAbsolutePath() + "//src//xmlStorage.xml";
        //fileManager = new FileManager(collectionSet, path);//"C://Java//Lab5//src//xmlStorage.xml");
        //collection.addAll(collectionSet);
        //load();
        //System.out.println(Worker.getWorkerIdMap().size() + " HJ " + Worker.getWorkerIdMap().toString());
        //Worker.getWorkerIdMap().forEach((aLong, worker) -> nextId = (aLong > nextId ? aLong : nextId));
        description.put("help", "вывести справку по доступным командам");//!
        description.put("info", "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д."); // done
        description.put("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении"); // done
        description.put("add", "добавить новый элемент в коллекцию | args: <element>"); // done
        description.put("update", "обновить значение элемента коллекции, id которого равен заданному | args: <id> <element>"); // done
        description.put("remove_by_id", "удалить элемент из коллекции по его id | args: <id>"); // done
        description.put("clear", "очистить коллекцию"); // done
        description.put("save", "сохранить коллекцию в файл"); // done
        description.put("execute_script", "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме | args: <filename>"); // done
        description.put("exit", "завершить без сохранения в файл"); // done
        description.put("add_if_max", "добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции | args: <element>"); // done
        description.put("add_if_min", "добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции | args: <element>"); //!!! +
        description.put("remove_lower", "удалить из коллекции все элементы, меньшие, чем заданный"); // done
        description.put("sum_of_salary", "вывести сумму значений поля salary для всех элементов коллекции"); // !!! +
        description.put("count_less_than_end_date", "вывести количество элементов, значение поля endDate которых меньше заданного | args: <endDate>"); // !!! +
        description.put("print_field_ascending_person", "вывести значения поля person всех элементов в порядке возрастания"); // !!!
    }

    public void init() {
        collectionSet = dbHandler.loadCollectionFromDB();
       // Set temp = Collections.synchronizedSet(collectionSet);
        //collection.addAll(temp);
        collection = Collections.synchronizedSet(collectionSet);
        //listType = collection.getClass().getSimpleName();
    }

    /**
     * Prints out the list of available commands with their descriptions.
     */
    public Response help() {
        StringBuilder msg = new StringBuilder();
        Set keySet = description.keySet();
        keySet.stream().forEach(key -> msg.append(key + " - " + description.get(key) + "\n"));
        return new Response(msg.toString());
        //  Почему-то работает только на стороне сервера
    }


    /**
     * @return String representation of an collection.
     */
    @Override
    public String toString() {
        return "Тип коллекции: " + listType + "\nДата инициализации: " + initDate + "\nКоличество объектов: " + collection.size();
    }

    /**
     * Shows the objects stored in collection with short description.
     */
    /*public void showCollection() {
        for (Object t: collection) {
            System.out.println(t.toString());
        }
    }*/

    public Response showCollection() {
            StringBuilder s = new StringBuilder();
            collection.forEach(t -> s.append(t.toString() + "\n"));
            return new Response(s.toString());
    }

    /**
     * Adds an object to a collection interactively.
     //* @param name The name of an object
     //* @param salary The price of an object
     * @param ifMax If true the object will be added only if it's greater than the max one in the collection. Needed for "add_if_max" command
     */
    /*public void add(String name, long salary, boolean ifMax, boolean ifMin) {
        Worker t = new Worker(name,
                Updater.updateCoordinates(),
                salary,
                Updater.updateStartDate(),
                Updater.updateEndDate(),
                Updater.updateStatus(),
                Updater.updatePerson());
//
        if (!(ifMax || ifMin)){
            collection.add(t);
            System.out.printf("\nДобавлен элемент с именем %s и ценой %d; его id: %d\n", name, salary, t.getId());
            return;
        }
        if(ifMax) {
            if(t.compareTo(collection.first()) < 0){
                collection.add(t);
                System.out.printf("\nДобавлен элемент с именем %s и ценой %d; его id: %d\n", name, salary, t.getId());
            }
            else
                System.out.println("Элемент не добавлен в коллекцию, так как он не максимален.");
        }
        if(ifMin) {
            if(t.compareTo(collection.last()) > 0) {
                collection.add(t);
                System.out.printf("\nДобавлен элемент с именем %s и ценой %d; его id: %d\n", name, salary, t.getId());
            }
            else
                System.out.println("Элемент не добавлен в коллекцию, так как он не минимален.");
        }
    }*/
    public Response add(Worker t, boolean ifMax, boolean ifMin) {
        //collectionLocker.writeLock().lock();
  //      try{
        synchronized (collection) {
            TreeSet<Worker> temp = new TreeSet<>();
            temp.addAll(collection);
            if (!(ifMax || ifMin) || (ifMax && (t.compareTo(temp.last()) > 0)) ||
                    (ifMin && (t.compareTo(temp.first()) < 0))) {
                collection.add(t);
                  return new Response("Добавлен объект: " + t.toString());
            }
        }
//        } finally {
//            collectionLocker.writeLock().unlock();
//        }
       // return new Response("Добавлен объект: " + t.toString());
        return new Response("Объект не добален, т. к. он не удовлетворяет условию добавления");
    }

    /**
     * Consequently updates all the fields of an object with id specified. Asks user if they wish to update the field.
     * @param id - id of an object to update.
     */
   /* public void updateId(long id) {
        try {
            Worker t = Worker.getWorkerById(id);
            if (t == null) throw new NullPointerException("Нет объекта с указанным id");
            if (Updater.ask("Хотите изменить имя работника?")) {
                t.setName(Updater.updateName());
            }
            if (Updater.ask("Хотите изменить зарплату работника?")) {
                t.setSalary(Updater.updateSalary());
            }
            if (Updater.ask("Хотите изменить время начала работы?")) {
                t.setStartDate(Updater.updateStartDate());
            }
            if (Updater.ask("Хотите изменить дату окончания работы?")) {
                t.setEndDate(Updater.updateEndDate());
            }
            if (Updater.ask("Хотите изменить статус работника")) {
                t.setStatus(Updater.updateStatus());
            }
            if (Updater.ask("Хотите изменить персональные данные работника")) {
                t.setPerson(Updater.updatePerson());
            }
            if (Updater.ask("Хотите изменить координаты работника?")) {
                t.setCoordinates(Updater.updateCoordinates());
            }
            System.out.printf("Билет с id %d успешно обновлен\n", id);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }*/
    public Response updateId(long id, Worker newT) {
//        collectionLocker.writeLock().lock();
//        try {
        synchronized (collection) {
            collection.remove(Worker.getWorkerById(id));
            collection.add(newT);
        }//finally {
//            collectionLocker.writeLock().unlock();
//        }
        return new Response("Элемент с указанным id успешно обновлён.");
    }


    /**
     * @param id Id of an object to be removed.
     */
   /* public void removeById(long id) {
        if (!collection.remove(Worker.getWorkerById(id))) { // if the element is not found remove() returns false
            System.out.println("Элемент с указанным id не найден.");
        }
        else {
            Worker.removeFromIdMap(id); // remove the element from (id -> Ticket) hashmap
            System.out.println("Элемент с id " + id + " успешно удалён.");
        }
    }*/
    public Response removeById(long id) {
//        collectionLocker.writeLock().lock();
//        try {
        synchronized (collection) {
            Worker worker = collection.stream().filter(w -> w.getId() == id).findFirst().orElse(null);
            if (worker == null) { // if the element is not found remove() returns false
                return new Response("Элемент с указанным id не найден.");
            } else {
                collection.remove(worker);
                Worker.removeFromIdMap(id); // remove the element from (id -> Ticket) hashmap
                return new Response("Элемент с id " + id + " успешно удалён.");
            }
        } //finally {
            //collectionLocker.writeLock().unlock();
        //}
    }

   /* public void showById(long id) {
        if (!collection.contains(Worker.getWorkerById(id))) { // if the element is not found remove() returns false
            System.out.println("Элемент с указанным id не найден.");
        }
        else {
            System.out.println(Worker.getWorkerById(id).toString());
        }
    }*/

    /**
     * Clears the collection
     */
    /*public void clear() {
        collection.clear(); // clear the collection
        Worker.resetId(); // reset the id counter
        System.out.println("Коллекция очищена.");
    }*/
//    public Response clear() {
//        collection.clear(); // clear the collection
//        Worker.resetId(); // reset the id counter
//        return new Response("Коллекция успешно очищена.");
//    }
    public Response clear(String initiator) {
//        collectionLocker.writeLock().lock();
//        try {
        synchronized (collection) {
            ArrayList<Worker> temp = new ArrayList<>(collection);
            Iterator<Worker> it = temp.iterator();
            while (it.hasNext()) {
                try {
                    Worker t = it.next();
                    dbHandler.removeWorkerByID(t.getId(), initiator);
                    collection.remove(t);
                } catch (InsufficientPermissionException ignored) {
                    //if the initiator is not the owner - just ignore
                } catch (SQLException e) {
                    e.printStackTrace();
                    return new Response("DB Error", false);
                }
            }
        } //finally {
            //collectionLocker.writeLock().unlock();
        //}
        return new Response("Success", true);
    }
    /**
     * Saves the collection to XML-File via FileManager
     */
   // @Deprecated
    public void saveToFile() {
        if (fileManager.isRead()) {
        collectionSet.clear();
        collectionSet.addAll(collection);
        fileManager.saveCollectionToFile();
        } else {
           System.out.println("Файл не был прочитан. Запись невозможна.");
        }
    }

    /**
     * Asks the user to input the object and removes all the objects which are lower (according to compareTo() method)
     */
   /* public void removeLower() {
        System.out.println("Укажите параметры сравниваемого объекта:");
        int count = 0;
        Worker worker = new Worker(Updater.updateName(),
                Updater.updateCoordinates(),
                Updater.updateSalary(),
                Updater.updateStartDate(),
                Updater.updateEndDate(),
                Updater.updateStatus(),
                Updater.updatePerson());
        LinkedHashSet <Worker> F = new LinkedHashSet<>();
        for (Worker t: collection) {
            if (t.compareTo(worker) < 0) {
                F.add(t);
                Worker.removeFromIdMap(t.getId());
                count += 1;
                System.out.println("Удалён объект: " + t.toString());
            }
        }
        collection.removeAll(F);
        System.out.println("Операция завершена. Объектов удалено: " + count);
    }*/
    public Response removeLower(Worker worker, String initiator) {
        int count = 0;
//        collectionLocker.writeLock().lock();
//        try{
        synchronized (collection) {
        LinkedHashSet<Worker> F = new LinkedHashSet<>();
        //StringBuilder sb = new StringBuilder();
        for (Worker t : collection) {
            if (t.compareTo(worker) < 0) {
                try {
                    F.add(t);
                    dbHandler.removeWorkerByID(t.getId(),initiator);
                    Worker.removeFromIdMap(t.getId());
                    count += 1;
                   // sb.append("Удалён объект: " + t.toString() + '\n');
                } catch (InsufficientPermissionException e) {
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
        collection.removeAll(F);
        return new Response(String.valueOf(count), true);
        }//finally {
         ///   collectionLocker.writeLock().unlock();
        //}
           // sb.append("Всего удалено объектов:" + count + '\n');
            //return new Response(sb.toString());
    }



       // System.out.println("Операция завершена. Объектов удалено: " + count);
        //collection.stream().filter(t -> t.compareTo(worker) < 0).forEach(t -> {
          //  collection.remove(t);
            //Worker.removeFromIdMap(t.getId());
            //sb.append("Удален объект: " + t.toString() + "\n");
       // });



    /*public void sumOfSalary(){
        long sum = 0;
        for(Worker o : collection){
            sum += o.getSalary();
        }
        System.out.println("Сумма запрлат всех сотрудников: " + sum);
    }*/
    public Response sumOfSalary(){
        int sum = 0;
        for(Worker o : collection){
            sum += o.getSalary();
        }
        return new Response("Сумма запрлат всех сотрудников: " + sum);
    }

   /* public void countLessThanEndDate(String s){
        long count = 0;
        try {
            Date date = Parsers.parseTheDate(s);
            for(Worker o : collection){
                if(o.getEndDate() == null)
                    continue;
                if(o.getEndDate().compareTo(date) < 0){
                    count += 1;
                }
            }
        } catch (DomainViolationException ex){
            ex.printMessage();
            return;
        }

        System.out.println("Количество элементов в коллекции , значение поля endDate которых меньше заданного: " + count);
    }*/
   public Response countLessThanEndDate(Date endDate) {
       long count = collection.stream().filter(worker -> (worker.getEndDate() != null && endDate.compareTo(worker.getEndDate()) > 0)).count();
       return new Response("Объектов в коллекции имеют значение endDate меньше заданного: " + count);
   }
   /*
    public void printPerson(){
        Set<Person> set = new HashSet<>();
        for(Worker o : collection){
            set.add(o.getPerson());
        }
        for(Person p : set){
            System.out.println(p.toString());
        }
    }*/
    public Response printPerson(){
        Set<Person> set = new HashSet<>();
        collection.forEach(t -> set.add(t.getPerson()));
        return new Response(set.toString());
    }
    /**
     * Reads and loads the collection from the XML-File via FileManager.
     */
    public void load() {
        this.collectionSet = fileManager.parseCollectionFromFile();
        collection.addAll(collectionSet);
        fileManager.setCollection(collectionSet);
    }

    public Worker getWorkerById(long id) {
        return Worker.getWorkerById(id);
    }

    public Response getInfo() {
        return new Response(toString());
    }

    public long generateId() {
        return ++nextId;
    }
    
}
