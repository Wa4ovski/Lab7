package common;

import java.io.Serializable;
import common.commands.AbstractCommand;

public class Request implements Serializable {

    private static final long serialVersionUID = -6213323027290265345L;
    private AbstractCommand command;
    private String token;
    private String initiator;

    public Request(AbstractCommand command) {
        this.command = command;
    }

    public Request(AbstractCommand command, String token) {
        this.command = command;
        this.token = token;
    }

    public AbstractCommand getCommand() {
        return this.command;
    }

    public boolean isEmpty() {
        return command == null;
    }

    public String getToken() {
        return token;
    }

    public void addToken(String token) {
        this.token = token;
    }

    public void addInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getInitiator() {
        return initiator;
    }

}
