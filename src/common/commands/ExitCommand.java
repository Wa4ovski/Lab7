package common.commands;

import common.Parsers;
import common.Request;
import common.exceptions.InvalidAmountOfArgumentsException;

public class ExitCommand extends AbstractCommand {
    public ExitCommand() {
        super(CommandType.EXIT);
    }

    @Override
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