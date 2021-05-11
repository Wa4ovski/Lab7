package common.commands;

import common.Parsers;
import common.Request;
import common.exceptions.*;
public class RemoveByIdCommand extends AbstractCommand{

    private long id;

    public RemoveByIdCommand() {
        super(CommandType.REMOVE_BY_ID);
    }


    @Override
    public Request execute(String[] commandSplit) {
        try {
            Parsers.verify(commandSplit, 1);
            this.id = Parsers.parseTheId(commandSplit[1]);
            return getRequest();
        } catch (DomainViolationException e) {
            e.printMessage();
        } catch (NumberFormatException e) {
            System.out.println("Ожидается число типа double. Проверьте, что введенное число не нарушает границ double");
        } catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        }
        return null;
    }

    public long getId() {
        return this.id;
    }
}