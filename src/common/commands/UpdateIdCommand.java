package common.commands;

import common.Parsers;
import common.Request;
import common.Updater;
import common.exceptions.InvalidAmountOfArgumentsException;
import common.model.Worker;

public class UpdateIdCommand extends AbstractCommand {

    private long id;
    private Worker newWorker;

    private boolean updateName;
    private boolean updateSalary;
    private boolean updateStartDate;
    private boolean updateEndDate;
    private boolean updateStatus;
    private boolean updatePerson;
    private boolean updateCoordinates;

    public boolean isUpdateName() {
        return updateName;
    }

    public boolean isUpdateSalary() {
        return updateSalary;
    }

    public boolean isUpdateStartDate() {
        return updateStartDate;
    }

    public boolean isUpdateEndDate() {
        return updateEndDate;
    }

    public boolean isUpdateStatus() {
        return updateStatus;
    }

    public boolean isUpdatePerson() {
        return updatePerson;
    }

    public boolean isUpdateCoordinates() {
        return updateCoordinates;
    }

    public UpdateIdCommand() {
        super(CommandType.UPDATE_ID);
    }

    public long getId() {
        return id;
    }

    public Worker getNewWorker() {
        return newWorker;
    }




    @Override
    public Request execute(String[] commandSplit) {
        try {
            Parsers.verify(commandSplit, 1);
            long id = Long.parseLong(commandSplit[1]);
            this.id = id;
            Worker t = new Worker();
            if (Updater.ask("Хотите изменить имя работника?")) {
                t.setName(Updater.updateName());
                updateName = true;
            }
            if (Updater.ask("Хотите изменить зарплату работника?")) {
                t.setSalary(Updater.updateSalary());
                updateSalary = true;
            }
            if (Updater.ask("Хотите изменить время начала работы?")) {
                t.setStartDate(Updater.updateStartDate());
                updateStartDate = true;
            }
            if (Updater.ask("Хотите изменить дату окончания работы?")) {
                t.setEndDate(Updater.updateEndDate());
                updateEndDate = true;
            }
            if (Updater.ask("Хотите изменить статус работника")) {
                t.setStatus(Updater.updateStatus());
                updateStatus = true;
            }
            if (Updater.ask("Хотите изменить персональные данные работника")) {
                t.setPerson(Updater.updatePerson());
                updatePerson = true;
            }
            if (Updater.ask("Хотите изменить координаты работника?")) {
                t.setCoordinates(Updater.updateCoordinates());
                updateCoordinates = true;
            }
            this.newWorker = t;
            return getRequest();
        } catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат аргумента.");
        }
        return null;
    }
}