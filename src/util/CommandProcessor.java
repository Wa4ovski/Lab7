package util;

import util.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.time.*;
import java.time.LocalDateTime.*;

import exceptions.*;


/**
 * A class used to parse the commands and launch them via CollectionManager.
 */
public class CommandProcessor {
    private CollectionManager manager;
    private String command;
    private String[] commandSplit;
    private final List<String> scriptStack = new ArrayList<>();
    public static boolean fileMode;
    public static Scanner fileScanner;

    public CommandProcessor(CollectionManager manager) {
        this.manager = manager;
        this.command = "";
    }

    /**
     * Launches the command processor and enters the interactive mode.
     * @return 0 if the program is finished properly.
     */
    public int launch() {
        System.out.println("Интерактивный режим запущен. Введите 'help' для справки.");
        fileMode = false;
        manager.load();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (command.equals("exit")) {
                break; // if the previous command was exit we won't proceed
            }
            command = scanner.nextLine(); // reading the whole line typed
            commandSplit = command.trim().split(" "); // remove extra-spaces and split the command from the argument
            executeCommand(commandSplit);
        }
        return 0;
    }

    /**
     * Executes a script of commands read from the file.
     * @param filename The name of the file to be read from.
     */
    public void executeScript(String filename) {
        String[] commandSplit;
        scriptStack.add(filename);
        try {
            fileMode = true;
            File file = new File(filename);
            // System.out.println(file.getAbsoluteFile());
            CommandProcessor.fileScanner = new Scanner(new BufferedInputStream(new FileInputStream(file)));
            if (!fileScanner.hasNext()) throw new NoSuchElementException();
            do {
                commandSplit = fileScanner.nextLine().trim().split(" ");
                if (commandSplit[0].equals("execute_script")) {
                    for (String script : scriptStack) {
                        if (commandSplit[1].equals(script)) throw new ScriptRecursionException("Рекурсия");
                    }
                }
                executeCommand(commandSplit);
            } while (fileScanner.hasNextLine());
        } catch (FileNotFoundException fnf) {
            System.out.println("Файл не найден.");
        } catch (NoSuchElementException e) {
            System.out.println("Ошибка исполнения скрипта. Проверьте правильность введенных в файл данных.");
        } catch (ScriptRecursionException e) {
            System.out.println("Рекурсия недопустима!!!");
        }
        finally {
            scriptStack.remove(scriptStack.size() - 1);
        }
        fileMode = false;
    }

    /**
     * Verifies if the command has a proper amount of arguments.
     * @param cmdSplit Command split into pieces
     * @param argsAmount Amount of arguments required.
     * @return {@code true} if the command has a proper amount of arguments. Otherwise, the exception will be thrown.
     * @throws InvalidAmountOfArgumentsException thrown if the amount of arguments doesn't equal the specified number
     */
    public static boolean verify(String[] cmdSplit, int argsAmount) throws InvalidAmountOfArgumentsException {
        boolean ver = cmdSplit.length == argsAmount + 1;
        if (!ver) throw new InvalidAmountOfArgumentsException(argsAmount);
        return true;
    }

    /**
     * Parses the string representation of a height into int. Checks if the height entered meets the domain requirements.
     * @param s The string to be parsed.
     * @return The parsed integer height.
     * @throws DomainViolationException thrown if the price doesn't match the domain
     */
    public static int parseTheHeigth(String s) throws DomainViolationException {
        int height = Integer.parseInt(s);
        if (!(height > 0)) throw new DomainViolationException("Рост человека должен быть положительным.");
        return height;
    }

    public static LocalDateTime parseTheLocalDateTime(String s) throws DomainViolationException{
        LocalDateTime dateTime = null;
        LocalDate date = null;
        //try {
            Date input = parseTheDate(s);
            try {
               date = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() ;
            }catch (NullPointerException exx){
                date = null;
            }
            if(date == null) throw new  DomainViolationException("Некоректный ввод даты");
            LocalTime time = LocalTime.parse("00:00:00");
            dateTime = date.atTime(time);
//        } catch (DomainViolationException ex){
//            ex.printMessage();
//        }
            return dateTime;

    }
    public static Date parseTheDate(String s)  throws DomainViolationException {
        if (s == null || s.isEmpty()) {
            //System.out.println("dkmcdk");
            return null;
        }
        SimpleDateFormat format = new StrictSimpleDateFormat("yyyy-MM-dd");
        //System.out.println(s.length());
        Date date = format.parse(s, new ParsePosition(0));
        if(date == null) throw new  DomainViolationException("Некоректный ввод даты");
        return date;
    }

    public static String DateToString(Date date){
        if(date == null)
            return "null";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String s = format.format(date);
        return s;
    }


    public static long parseTheSalary(String s) throws DomainViolationException{
        long salary = Long.parseLong(s);
        if (!(salary > 0)) throw new DomainViolationException("Поле salary должно быть больше 0.");
        if (!(salary < Long.MAX_VALUE)) throw new DomainViolationException("Число не входит в область типа long");
        return salary;
    }

    /**
     * Parses the string representation of an id field into long. Checks the domain conflict.
     * @param s - The string to be parsed
     * @return Long ID value parsed
     * @throws DomainViolationException - thrown if the number given doesn't match the requirements.
     */
    public static long parseTheId(String s) throws DomainViolationException {
        long id = Long.parseLong(s);
        if (!(id > 0)) throw new DomainViolationException("Поле id должно быть больше 0.");
        if (!(id < Long.MAX_VALUE)) throw new DomainViolationException("Число не входит в область типа long");
        return id;
    }

    /**
     * Executes the entered command.
     * @param commandSplit The command split into parts to differentiate the command and arguments.
     */
    public void executeCommand(String[] commandSplit) {
        try {
            switch (commandSplit[0]) { // define the "operation" part of a command
                case "":
                    break;
                case "help":
                    manager.help();
                    break;
                case "info":
                    System.out.println(manager.toString());
                    break;
                case "show":
                    manager.showCollection();
                    break;
                case "add":
                    if(verify(commandSplit, 2)) { // verify the proper amount of args
                        try {
                            manager.add(commandSplit[1], parseTheSalary(commandSplit[2]), false, false);
                        } catch (NumberFormatException nfe) {
                            System.out.println("Ошибка ввода зарплаты: ожидается long");
                        } catch (DomainViolationException dve) {
                            dve.printMessage();
                        }
                    }
                    // if no argument stated -> ArrayIndexOutOfBoundsException will be thrown and caught
                    break;
                case "update":
                    verify(commandSplit, 1);
                    try {
                        long id = parseTheId(commandSplit[1]);
                        manager.updateId(id);
                    } catch (DomainViolationException e) {
                        e.printMessage();
                    } catch (NumberFormatException e) {
                        System.out.println("Ожидается число типа double. Проверьте, что введенное число не нарушает границ double.");
                    }
                    break;
                case "remove_by_id":
                    verify(commandSplit, 1);
                    try {
                        long id = parseTheId(commandSplit[1]);
                        manager.removeById(id);
                    } catch (DomainViolationException e) {
                        e.printMessage();
                    } catch (NumberFormatException e) {
                        System.out.println("Ожидается число типа double. Проверьте, что введенное число не нарушает границ double");
                    }
                case "show_by_id":
                    verify(commandSplit, 1);
                    try {
                        long id = parseTheId(commandSplit[1]);
                        manager.showById(id);
                    } catch (DomainViolationException e) {
                        e.printMessage();
                    } catch (NumberFormatException e) {
                        System.out.println("Ожидается число типа double. Проверьте, что введенное число не нарушает границ double");
                    }
                    break;
                case "clear":
                    verify(commandSplit, 0);
                    manager.clear();
                    break;
                case "save":
                    verify(commandSplit, 0);
                    manager.saveToFile();
                    break;
                case "execute_script":
                    verify(commandSplit, 1);
                    executeScript(commandSplit[1]);
                    break;
                case "exit":
                    verify(commandSplit, 0);
                    break; // probably we don't have to do anything
                case "add_if_max":
                    try {
                        manager.add(commandSplit[1], parseTheSalary(commandSplit[2]), true, false);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Ошибка ввода зарплаты: ожидается long");
                    } catch (DomainViolationException dve) {
                        System.out.println("Ожидается положительное число формата long");
                    }
                    break;
                case "add_if_min":
                    try {
                        manager.add(commandSplit[1], parseTheSalary(commandSplit[2]), false, true);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Ошибка ввода зарплаты: ожидается long");
                    } catch (DomainViolationException dve) {
                        System.out.println("Ожидается положительное число формата long");
                    }

                    break;
                case "remove_lower":
                    verify(commandSplit, 0);
                    manager.removeLower();
                    break;
                case "sum_of_salary":
                    manager.sumOfSalary();
                    break;
                case "count_less_than_end_date":
                    verify(commandSplit, 1);
                    manager.countLessThanEndDate(commandSplit[1]);

                      //  System.out.println("Ожидается дата в формате гггг-мм-дд");

                    break;
                case "print_field_ascending_person":
                    manager.printPerson();
                    break;

                default:
                    System.out.println("Invalid command. Type 'help' to show available commands.");
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException oob) {
            System.out.println("Argument read error. Type 'help' for more.");
        } catch (InvalidAmountOfArgumentsException e) {
            System.out.println("Error. Expected " + e.getRequiredAmount() + " arguments");
        }
    }
}
