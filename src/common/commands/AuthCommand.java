package common.commands;

import common.exceptions.*;
import common.*;

import java.io.Console;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AuthCommand extends AbstractCommand {

    public static final String USERNAME_REGEXP = "[0-9A-Za-z]{3,12}";
    public static final String PASSWORD_REGEXP = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z!@#$%^&*]{6,30}";
    private AuthType authType;
    private String username;
    private String password;

    public AuthCommand() {
        super(CommandType.AUTH);
    }

    @Override
    public Request execute(String[] commandSplit) {
        try {
            Parsers.verify(commandSplit, 2);
            String authType = commandSplit[1];
            String username = commandSplit[2];
            String password;
            defineType(authType);
            Console console = System.console();
            Scanner scanner = new Scanner(System.in);
            if (usernameIsCorrect(username)) this.username = username;
            while (true) {
                try {
                    System.out.println("Введите пароль:");
                    if (console != null) {
                        char[] symbols = console.readPassword();
                        if (symbols == null) {
                            System.out.println("Получен сигнал конца ввода. Завершение работы.");
                            System.exit(0);
                        };
                        password = String.valueOf(symbols);
                    }
                    else password = scanner.nextLine();
                    if (passwordIsCorrect(password)) break;
                } catch (DomainViolationException e) {
                    e.printMessage();
                }
            }
            this.password = password;
            return getRequest();
        } catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        } catch (InvalidAuthTypeException e) {
            e.printMessage();
        } catch (DomainViolationException e) {
            e.printMessage();
        }

        return new Request(null);
    }

    private void defineType(String stringAuthType) throws InvalidAuthTypeException {
        switch (stringAuthType) {
            case "login":
                this.authType = AuthType.LOGIN;
                break;
            case "reg":
                this.authType = AuthType.REGISTER;
                break;
            default:
                throw new InvalidAuthTypeException("Invalid auth argument. Expected 'login' or 'reg'.");
        }
    }

    private boolean usernameIsCorrect(String username) throws DomainViolationException {
        boolean match = Pattern.matches(USERNAME_REGEXP, username);
        if (!match) throw new DomainViolationException("Invalid username.");
        return true;
    }

    private boolean passwordIsCorrect(String password) throws DomainViolationException {
        boolean match = Pattern.matches(PASSWORD_REGEXP, password);
        if (!match) throw new DomainViolationException("Invalid password.");
        return true;
    }

    public AuthType getAuthType() {
        return this.authType;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public enum AuthType {
        LOGIN, REGISTER
    }
}
