package client;

import common.CommandProcessor;
import server.CollectionManager;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("Клиент запущен");
        Client client = new Client("localhost", 2408, new CommandProcessor());
        client.start();
    }
}
