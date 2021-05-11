package common;

import common.*;
import common.exceptions.DomainViolationException;
import common.exceptions.InvalidAmountOfArgumentsException;
import util.StrictSimpleDateFormat;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class Parsers {
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
            return null;
        }
        SimpleDateFormat format = new StrictSimpleDateFormat("yyyy-MM-dd");
        //System.out.println(s.length());
        Date date = format.parse(s, new ParsePosition(0));
        if(date == null) throw new  DomainViolationException("Некоректный ввод даты");
        return date;
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
}
