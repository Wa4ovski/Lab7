package common.commands;

import common.Parsers;
import common.Request;
import common.exceptions.InvalidAmountOfArgumentsException;

public class ClearCommand extends AbstractCommand {

    public ClearCommand() {
        super(CommandType.CLEAR);
    }

    public Request execute(String[] commandSplit) {
        try{
            Parsers.verify(commandSplit, 0);
            return getRequest();
        } catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        }
        return null;
    }
}