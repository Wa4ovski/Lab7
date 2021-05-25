package server;


import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerMain {
    public static final int PORT = 2408;

    public static void main(String[] args) {
        //FileManager fileManager = new FileManager();
       // Class.forName("org.postgresql.Driver");
        Scanner credentials = null;
        String username = null;
        String password = null;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Не найден POSTGRESQL драйвер.");
            System.exit(-1);
        }

        try {
            credentials = new Scanner(new FileReader("credentials.txt"));
        } catch (FileNotFoundException e) {
            System.err.println("Не найден credentials.txt с данными для входа в базу данных.");
            System.exit(-1);
        }
        String jdbcURL = "jdbc:postgresql://localhost:2806/studs";
        try {
            username = credentials.nextLine().trim();
            password = credentials.nextLine().trim();
        } catch (NoSuchElementException e) {
            System.err.println("Не найдены данные для входа в файле. Завершение работы.");
            System.exit(-1);
        }

        DatabaseHandler dbHandler = new DatabaseHandler(jdbcURL, username, password);
       // CollectionManager cm = new CollectionManager(dbHandler);
        dbHandler.connectToDatabase();
       // cm.init();


        System.out.println("Введите имя файла колллекции:");
        CollectionManager.path  = new Scanner(System.in).nextLine();
        CollectionManager cm = new CollectionManager();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                cm.saveToFile();
            }
        });
        ServerRequestHandler requestManager = new ServerRequestHandler(cm);
        Server server = new Server(PORT, requestManager);
        server.run();
        cm.saveToFile();
    }
}