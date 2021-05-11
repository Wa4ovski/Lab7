package common.commands;

import common.Parsers;
import common.Request;
import common.exceptions.InvalidAmountOfArgumentsException;

public class SumOfSalaryCommand extends AbstractCommand {
    public SumOfSalaryCommand() {
        super(CommandType.SUM_OF_SALARY);
    }

    @Override
    public Request execute(String[] commandSplit) {
        try {
            Parsers.verify(commandSplit, 0);
            return getRequest();
        } catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        }
        return new Request(null);
    }

}
