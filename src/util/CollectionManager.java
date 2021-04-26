package util;

import exceptions.DomainViolationException;
import model.Person;
import model.Worker;

import java.io.File;
import java.util.*;

public class CollectionManager {
    private LinkedHashSet<Worker> collectionSet;
    private TreeSet<Worker> collection = new TreeSet<>();
    private static HashMap<String, String> description;
    private String listType;
    private final Date initDate;
    private FileManager fileManager;

    {
        collectionSet = new LinkedHashSet<Worker>();
        initDate = new Date();
        description = new HashMap<String, String>();
        listType = collection.getClass().getSimpleName();
        File file = new File("");
        String path = "" + file.getAbsolutePath() + "//src//xmlStorage.xml";
        fileManager = new FileManager(collectionSet, path);//"C://Java//Lab5//src//xmlStorage.xml");
        collection.addAll(collectionSet);
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

    /**
     * Prints out the list of available commands with their descriptions.
     */
    public void help() {
        for (String key : description.keySet()) {
            System.out.println(key + " - " + description.get(key));
        }
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
    public void showCollection() {
        for (Object t: collection) {
            System.out.println(t.toString());
        }
    }

    /**
     * Adds an object to a collection interactively.
     * @param name The name of an object
     * @param salary The price of an object
     * @param ifMax If true the object will be added only if it's greater than the max one in the collection. Needed for "add_if_max" command
     */
    public void add(String name, long salary, boolean ifMax, boolean ifMin) {
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
    }

    /**
     * Consequently updates all the fields of an object with id specified. Asks user if they wish to update the field.
     * @param id - id of an object to update.
     */
    public void updateId(long id) {
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
    }


    /**
     * @param id Id of an object to be removed.
     */
    public void removeById(long id) {
        if (!collection.remove(Worker.getWorkerById(id))) { // if the element is not found remove() returns false
            System.out.println("Элемент с указанным id не найден.");
        }
        else {
            Worker.removeFromIdMap(id); // remove the element from (id -> Ticket) hashmap
            System.out.println("Элемент с id " + id + " успешно удалён.");
        }
    }

    public void showById(long id) {
        if (!collection.contains(Worker.getWorkerById(id))) { // if the element is not found remove() returns false
            System.out.println("Элемент с указанным id не найден.");
        }
        else {
            System.out.println(Worker.getWorkerById(id).toString());
        }
    }

    /**
     * Clears the collection
     */
    public void clear() {
        collection.clear(); // clear the collection
        Worker.resetId(); // reset the id counter
        System.out.println("Коллекция очищена.");
    }

    /**
     * Saves the collection to XML-File via FileManager
     */
    public void saveToFile() {
        collectionSet.clear();
        collectionSet.addAll(collection);
        fileManager.saveCollectionToFile();
    }

    /**
     * Asks the user to input the object and removes all the objects which are lower (according to compareTo() method)
     */
    public void removeLower() {
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
    }

    public void sumOfSalary(){
        long sum = 0;
        for(Worker o : collection){
            sum += o.getSalary();
        }
        System.out.println("Сумма запрлат всех сотрудников: " + sum);
    }

    public void countLessThanEndDate(String s){
        long count = 0;
        try {
            Date date = CommandProcessor.parseTheDate(s);
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
    }

    public void printPerson(){
        Set<Person> set = new HashSet<>();
        for(Worker o : collection){
            set.add(o.getPerson());
        }
        for(Person p : set){
            System.out.println(p.toString());
        }
    }
    /**
     * Reads and loads the collection from the XML-File via FileManager.
     */
    public void load() {
        this.collectionSet = fileManager.parseCollectionFromFile();
        collection.addAll(collectionSet);
        fileManager.setCollection(collectionSet);
    }

    
}
