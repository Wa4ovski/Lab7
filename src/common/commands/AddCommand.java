package common.commands;

import common.Parsers;
import common.Request;
import common.Updater;
import common.exceptions.DomainViolationException;
import common.exceptions.InvalidAmountOfArgumentsException;
import common.model.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.NoSuchElementException;

public class AddCommand extends AbstractCommand {
    private Worker worker;
    private boolean ifMax;
    private boolean ifMin;

    public AddCommand(boolean ifMax, boolean ifMin) {
        super(CommandType.ADD);
        this.ifMax = ifMax;
        this.ifMin = ifMin;
    }

    public Request execute(String[] commandSplit) {
        try {
            Parsers.verify(commandSplit, 2);
            String name = commandSplit[1];
            long salary = Parsers.parseTheSalary(commandSplit[2]);
            Coordinates coordinates = Updater.updateCoordinates();
            Status status = Updater.updateStatus();
            LocalDateTime startDate = Updater.updateStartDate();
            Date endDate = Updater.updateEndDate();
            Person person = Updater.updatePerson();
            Worker t = new Worker(name,
                    coordinates,
                    salary,
                    startDate,
                    endDate,
                    status,
                    person);
            this.worker = t;
            return getRequest();
        } catch (NoSuchElementException e) {
            System.out.println("Получен сигнал конца ввода. Завершение.");
        } catch (DomainViolationException dve) {
            dve.printMessage();
        } catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат данных.");
        }
        return new Request(null);
    }

    public Worker getWorker() {
        return worker;
    }

    public boolean ifMax() {
        return ifMax;
    }

    public boolean ifMin() {
        return ifMin;
    }
}
