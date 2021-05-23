package common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;


import common.commands.*;
import common.exceptions.*;
import server.CollectionManager;


/**
 * A class used to parse the commands and launch them via CollectionManager.
 */
public class CommandProcessor {
    //private CollectionManager manager;
    private String command;
    private String[] commandSplit;
    private final List<String> scriptStack = new ArrayList<>();
    public static boolean fileMode;
    public static Scanner fileScanner;

    public CommandProcessor() {
     //   this.manager = manager;
        this.command = "";
    }

    public String[] readCommand() {
        fileMode = false;
        Scanner scanner = new Scanner(System.in);
        if (command.equals("exit") || !(scanner.hasNext())) {
            System.out.println("Завершение программы.");
            System.exit(0);
        }
        command = scanner.nextLine();
        commandSplit = command.trim().split(" "); // remove extra-spaces and split the command from the argument
        return commandSplit;
    }
    /**
     * Verifies if the command has a proper amount of arguments.
     * @param cmdSplit Command split into pieces
     * @param argsAmount Amount of arguments required.
     * @return {@code true} if the command has a proper amount of arguments. Otherwise, the exception will be thrown.
     * @throws InvalidAmountOfArgumentsException thrown if the amount of arguments doesn't equal the specified number
     */
    /*
    public static boolean verify(String[] cmdSplit, int argsAmount) throws InvalidAmountOfArgumentsException {
        boolean ver = cmdSplit.length == argsAmount + 1;
        if (!ver) throw new InvalidAmountOfArgumentsException(argsAmount);
        return true;
    }*/

    /**
     * Launches the command processor and enters the interactive mode.
     * @return 0 if the program is finished properly.

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
    }*/

    /**
     * Executes a script of commands read from the file.
     * @param filename The name of the file to be read from.
     */
    public ArrayList<Request> executeScript(String filename) {
        String[] commandSplit;
        ArrayList<Request> script = new ArrayList<>();
        scriptStack.add(filename);
        try {
            fileMode = true;
            File file = new File(filename);
            System.out.println(file.getAbsoluteFile() + " dcs");
            System.out.println(filename);
            CommandProcessor.fileScanner = new Scanner(new BufferedInputStream(new FileInputStream(file)));
            if (!fileScanner.hasNext()) throw new NoSuchElementException();
            do {
                commandSplit = fileScanner.nextLine().trim().split(" ");
                if (commandSplit[0].equals("execute_script") && (scriptStack.contains(filename))) {
                    System.out.println("Рекурсия!!!'");
                } else {
                    System.out.println("scradd "+ commandSplit.toString());
                    script.add(generateRequest(commandSplit));
                }
            } while (fileScanner.hasNextLine());
        } catch (FileNotFoundException fnf) {
            System.out.println("Файл не найден.");
        } catch (NoSuchElementException e) {
            System.out.println("Ошибка исполнения скрипта. Проверьте правильность введенных в файл данных.");
        }
//        catch (ScriptRecursionException e) {
        //          System.out.println("Рекурсия недопустима!!!");
        //    }
        finally {
            scriptStack.remove(scriptStack.size() - 1);
        }
        fileMode = false;
        return script;
    }




    /**
     * Executes the entered command.
     * @param commandSplit The command split into parts to differentiate the command and arguments.
     */
    public Request generateRequest(String[] commandSplit) {
        try {
            switch (commandSplit[0]) { // define the "operation" part of a command
                case "":
                    break;
                case "help":
                    return new HelpCommand().execute(commandSplit);
                case "info":
                    return new InfoCommand().execute(commandSplit);
                case "show":
                    return new ShowCommand().execute(commandSplit);
                case "add":
                    return new AddCommand(false, false).execute(commandSplit);
                case "update":
                    return new UpdateIdCommand().execute(commandSplit);
                case "remove_by_id":
                    return new RemoveByIdCommand().execute(commandSplit);
                case "clear":
                    return new ClearCommand().execute(commandSplit);
            //    case "save":
                case "execute_script":
                   // executeScript(commandSplit[1]);//ExecuteScriptCommand().execute(commandSplit);
                    return new ExecuteScriptCommand().execute(commandSplit);
                case "exit":
                    break;
                case "add_if_max":
                   return new AddCommand(true, false).execute(commandSplit);
                case "add_if_min":
                    return new AddCommand(false, true).execute(commandSplit);
                case "remove_lower":
                   return new RemoveLowerCommand().execute(commandSplit);
                case "sum_of_salary":
                    return new SumOfSalaryCommand().execute(commandSplit);
                case "count_less_than_end_date":
                    return new CountLessThanEndDateCommand().execute(commandSplit);
                case "print_field_ascending_person":
                    return new PrintFieldAscendingPersonCommand() .execute(commandSplit);
                default:
                    System.out.println("Invalid command. Type 'help' to show available commands.");
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException oob) {
            System.out.println("Argument read error. Type 'help' for morehbhb.");
        }
        return new Request(null);
    }
}
