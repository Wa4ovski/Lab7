package server;

import java.util.Scanner;

public class ServerInputHandler extends Thread {

    private CollectionManager collectionManager;

    public ServerInputHandler(CollectionManager cm) {
        collectionManager = cm;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        do {
            if (!scanner.hasNext()) continue;
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                collectionManager.saveToFile();
                System.out.println("Завершение работы сервера...");
                System.exit(0);
            }
            else if (input.equals("save")) {
                collectionManager.saveToFile();
            }
            else {
                System.out.println("Неверная команада.");
            }

        } while (scanner.hasNext());
    }
}

