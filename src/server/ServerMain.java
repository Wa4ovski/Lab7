package server;

import java.io.IOException;
import java.util.Scanner;

public class ServerMain {
    public static final int PORT = 2408;

    public static void main(String[] args) {
        //FileManager fileManager = new FileManager();

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