package common.commands;

import common.Parsers;
import common.Request;
import common.Updater;
import common.exceptions.InvalidAmountOfArgumentsException;
import common.model.Worker;

public class RemoveLowerCommand extends AbstractCommand {

    private Worker worker;
    public RemoveLowerCommand() {
        super(CommandType.REMOVE_LOWER);
    }

    @Override
    public Request execute(String[] commandSplit) {
        try {
            Parsers.verify(commandSplit, 0);
            System.out.println("Укажите параметры сравниваемого объекта:");
            this.worker = new Worker(Updater.updateName(),
                    Updater.updateCoordinates(),
                    Updater.updateSalary(),
                    Updater.updateStartDate(),
                    Updater.updateEndDate(),
                    Updater.updateStatus(),
                    Updater.updatePerson());
            return getRequest();
        } catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        }
        return null;
    }

    public Worker getWorker() {
        return this.worker;
    }
}