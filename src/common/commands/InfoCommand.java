package common.commands;
import common.*;
import common.exceptions.InvalidAmountOfArgumentsException;

public class InfoCommand extends AbstractCommand {

    public InfoCommand() {
        super(CommandType.INFO);
    }

    @Override
    public Request execute(String[] commandSplit) {
        try {
            Parsers.verify(commandSplit, 0);
        } catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        }
        return getRequest();
    }
}