package util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Scanner;
import model.*;
import exceptions.*;

/**
 * A class containing methods to update the info of objects stored both from the file or keyboard.
 */
public class Updater {
    public static String updateName() {
        while (true) {
            System.out.print("Введите имя: ");
            String data;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim();
            else {
                data = CommandProcessor.fileScanner.nextLine().trim();
                System.out.println(data);
            }
            if (data.isEmpty()) System.out.println("Строка не может быть пустой");
            else {
                return data;
            }
        }
    }

    /**
     * Updates the Coordinates field.
     * @return new Coordinate value
     */
    public static Coordinates updateCoordinates() {
        while (true) {
            System.out.println("Укажите координаты x, y через пробел (x > -194): ");
            String[] data;
            String output;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim().split(" ");
            else {
                output = CommandProcessor.fileScanner.nextLine();
                System.out.println(output);
                data = output.trim().split(" ");
            }
            try {
                if (data.length != 2) throw new InvalidAmountOfArgumentsException(2);
                double x = Double.parseDouble(data[0]);
                float y = Float.parseFloat(data[1]);
                if (!(x > -194.0)) throw new DomainViolationException("x > -194 required");
                if (!(x < Double.MAX_VALUE)) throw new DomainViolationException("x is out of 'double' type bounds");
                if (!((y < Float.MAX_VALUE) && (y > Float.MIN_VALUE))) throw new DomainViolationException("y is out of 'float' type bounds");
                return new Coordinates(x, y);
            } catch (NumberFormatException nfe) {
                System.out.println("Неверный формат. Ожидается два числа x и y формата double и float соответственно (x > -194).");
            } catch (DomainViolationException dve) {
                System.out.println("Неверный формат. Координата должна быть > -194.");
            } catch (InvalidAmountOfArgumentsException e) {
                System.out.println("Ожидается два аргумента: x и у через пробел.");
            }
        }
    }
    /**
     * Updates the salary of an object.
     * @return new salary
     */
    public static long updateSalary() {
        while (true) {
            System.out.println("Введите зарплату сотрудника (положительное число формата long): ");
            String data;
            String output;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim();
            else {
                output = CommandProcessor.fileScanner.nextLine();
                System.out.println(output);
                data = output.trim();
            }
            try {
                long salary = CommandProcessor.parseTheId(data);
                return salary;
            } catch (DomainViolationException e) {
                e.printMessage();
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат числа. Повторите попытку ввода.");
            }
        }
    }

    public static LocalDateTime updateStartDate(){
        while (true){
            System.out.println("Введите дату и время начала работы сотрудника (в формате гггг-мм-дд):");
            String data;
            String output;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim();
            else {
                output = CommandProcessor.fileScanner.nextLine();
                System.out.println(output);
                data = output.trim();
            }
            try {
                LocalDateTime startDate = CommandProcessor.parseTheLocalDateTime(data);
                return startDate;
            }  catch (DomainViolationException e) {
             e.printMessage();
           }//!!!
        }
    }

    public static Date updateEndDate(){
        while (true){
            System.out.println("Введите дату окончания работы сотрудника (в формате гггг-мм-дд). Если сотрудник " +
                    "еще работает в фирме, нажмите Enter");
            String data;
            String output;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim();
            else {
                output = CommandProcessor.fileScanner.nextLine();
                System.out.println(output);
                data = output.trim();
            }

            try {
                Date endDate = CommandProcessor.parseTheDate(data);
                return endDate;
            } catch (DomainViolationException e) {
                e.printMessage();
            }//!!!
        }
    }

    /**
     * Updates the Status field.
     * @return new Status value
     */
    public static Status updateStatus() {
        while (true) {
            System.out.println("Выберите текущий статус работника из предложенных: ");
            for (Status type: Status.values()) {
                System.out.println(type);
            }
            String data;
            String output;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim().toUpperCase();
            else {
                output = CommandProcessor.fileScanner.nextLine();
                System.out.println(output);
                data = output.trim().toUpperCase();
            }
            try {
                return Status.valueOf(data);
            }catch (IllegalArgumentException iae) {
                    System.out.println("Неверный ввод.");
            }

        }
    }
    /**
     * Updates the Person field.
     * @return new Person value
     */
    public static Person updatePerson() {
        System.out.println("Ввод Person~");
        return new Person(updateBirthday(), updateHeight(), updatePassportID());
    }

    /**
     * Updates the birthday field of Person.
     * @return new birthday
     */
    public static LocalDateTime updateBirthday(){
        while (true){
            System.out.println("Введите дату рождения сотрудника (в формате гггг-мм-дд):");
            String data;
            String output;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim();
            else {
                output = CommandProcessor.fileScanner.nextLine();
                System.out.println(output);
                data = output.trim();
            }
            try {
                LocalDateTime birthday = CommandProcessor.parseTheLocalDateTime(data);
                return birthday;
            } catch (DomainViolationException e) {
                e.printMessage();
            }//!!!
        }
    }

    /**
     * Updates the height field of Person.
     * @return new height
     */
    public static int updateHeight() {
        while (true) {
            System.out.println("Введите рост человека (натуральное число типа int): ");
            String data;
            String output;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim();
            else {
                output = CommandProcessor.fileScanner.nextLine();
                System.out.println(output);
                data = output.trim();
            }
            try {
                int height = CommandProcessor.parseTheHeigth(data);
                return height;
            } catch (DomainViolationException e) {
                e.printMessage();
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат числа. Повторите попытку ввода.");
            }
        }
    }

    public static String updatePassportID() {
        while (true) {
            System.out.print("Введите номер паспорта (не более 38 символов): ");
            String data;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim();
            else {
                data = CommandProcessor.fileScanner.nextLine().trim();
                System.out.println(data);
            }
            if (data.isEmpty()) System.out.println("Строка не может быть пустой");
            if (!(data.length() <= 38)) System.out.println("Номер паспорта не может состоять более чем из 38 символов");
            else {
                return data;
            }
        }
    }

    /**
     * Asks user if they want to proceed updating the field. The user types answers with "y" or "n" via keyboard.
     * @param question - the question printed before reading from the keyboard
     * @return {@code true} if the user wants to update the specified field (y) or {@code false} if the user doesn't want to update the specified field (n)
     *
     */
    public static boolean ask(String question) {
        while (true) {
            System.out.println(question + " (y/n)");
            Scanner answer = new Scanner(System.in);
            switch (answer.nextLine().trim().toLowerCase()) {
                case "y":
                    return true;
                case "n":
                    return false;
                default:
                    System.out.println("Введите y или n.");
            }
        }
    }


}
