package client;

import common.CommandProcessor;
import server.CollectionManager;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("Клиент запущен");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя файла колллекции:");
        CollectionManager.path  = scanner.nextLine();
        Client client = new Client("localhost", 2308, new CommandProcessor());
        client.start();
        scanner.close();
    }
}
