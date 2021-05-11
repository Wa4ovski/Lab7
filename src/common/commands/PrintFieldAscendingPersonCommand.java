package common.commands;

import common.Parsers;
import common.Request;
import common.exceptions.InvalidAmountOfArgumentsException;

public class PrintFieldAscendingPersonCommand extends AbstractCommand {
    public PrintFieldAscendingPersonCommand() {
        super(CommandType.PRINT_FIELD_ASCENDING_PERSON);
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
