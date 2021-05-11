package common.commands;

import common.Parsers;
import common.Request;
import common.exceptions.DomainViolationException;
import common.exceptions.InvalidAmountOfArgumentsException;

import java.util.Date;

public class CountLessThanEndDateCommand extends AbstractCommand {
    private Date endDate;
    public CountLessThanEndDateCommand(){
        super(CommandType.COUNT_LESS_THAN_END_DATE);
    }

    public Request execute(String[] commandSplit){
        try {
            Parsers.verify(commandSplit, 1);
            this.endDate = Parsers.parseTheDate(commandSplit[1]);
            return getRequest();
        }
        catch (DomainViolationException e){
            System.out.println("Введенная дата должна быть в формате гггг-мм-дд");
        }
        catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        }catch (NumberFormatException e) {
            System.out.println("Неверный формат данных.");
        }catch (NullPointerException e) {
            System.out.println("aaaaa");
        }
        return null;
    }

    public Date getEndDate(){
        return this.endDate;
    }
}
