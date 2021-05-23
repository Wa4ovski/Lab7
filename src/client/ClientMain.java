package client;

import common.CommandProcessor;
import server.CollectionManager;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("Клиент запущен");
        Client client = new Client("localhost", 2308, new CommandProcessor());
        client.start();
    }
}
