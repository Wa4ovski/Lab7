package common.commands;


import common.*;
import common.exceptions.InvalidAmountOfArgumentsException;

public class HelpCommand extends AbstractCommand {

    public HelpCommand() {
        super(CommandType.HELP);
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
