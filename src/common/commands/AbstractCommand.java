package common.commands;

import java.io.Serializable;
import common.*;


public abstract class AbstractCommand implements Serializable {
    protected final CommandType commandType;
    public abstract Request execute(String[] commandSplit);

    public CommandType getCommandType() {
        return commandType;
    }



    protected AbstractCommand(CommandType commandType) {
        this.commandType = commandType;
    }


    public Request getRequest() {
        Request req = new Request(this);
        return req;
    }

}
