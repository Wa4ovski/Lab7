package server;

import common.CommandProcessor;
import common.Request;
import common.Response;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Server {
    private int port;
    private ServerRequestHandler requestManager;
    private DatagramSocket socket;
    private InetAddress address;
    private Scanner scanner;
   // private CommandProcessor cp;

    public Server(int port, ServerRequestHandler requestManager) {
        this.port = port;
        this.requestManager = requestManager;
    }

    public void run() {
        System.out.println("Запуск сервера!");
        boolean processStatus = true;
        scanner = new Scanner(System.in);
        Runnable userInput = () -> {
            try {
                while (true) {
                    String command = scanner.nextLine();
                    String[] commandSplit = command.trim().split(" "); // remove extra-spaces and split the command from the argument
                    if (commandSplit[0].equals("save")) {
                        System.out.println("Сервер не может сам принимать такую команду!");
                        return;
                    }
                    Response response = requestManager.processClientRequest(new CommandProcessor().generateRequest(commandSplit));
                    System.out.println(response.getResponseInfo());
                }
            } catch (Exception e) {}
        };
        Thread thread = new Thread(userInput);
        thread.start();
        while (processStatus) {
            processStatus = processingClientRequest();
        }
    }

    private boolean processingClientRequest(){
        Request request = null;
        Response response = null;
        try {
            socket = new DatagramSocket(2308);
            scanner = new Scanner(System.in);
            do {
                request = getRequest();
                System.out.println("Получена команда '" + request.getCommand().toString() + "'");//
                response = requestManager.processClientRequest(request);//executeRequest(request);
               // System.out.println(response.getResponseInfo());
                System.out.println("Команда '" + request.getCommand().toString() + "' выполнена");
                sendResponse(response);
              //  System.out.println(response.getResponseInfo());
            } while (!response.isEmpty());//(response.getResponseCode() != ResponseCode.SERVER_EXIT);
            return false;
        } catch (IOException | ClassNotFoundException exception) {
            System.out.println("Произошла ошибка при работе с клиентом!");
        }

        return true;
    }

    private Request getRequest() throws IOException, ClassNotFoundException {
        byte[] getBuffer = new byte[socket.getReceiveBufferSize()];
        DatagramPacket getPacket = new DatagramPacket(getBuffer, getBuffer.length);
        socket.receive(getPacket);
        address = getPacket.getAddress();
        port = getPacket.getPort();
        return deserialize(getPacket, getBuffer);
    }

    private void sendResponse(Response response) throws IOException {
        byte[] sendBuffer = serialize(response);
        System.out.println("bufL" + sendBuffer.length);
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
        socket.send(sendPacket);
    }

    private Response executeRequest(Request request) {
        return requestManager.processClientRequest(request);
    }

    private Request deserialize(DatagramPacket getPacket, byte[] buffer) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(getPacket.getData());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Request request = (Request) objectInputStream.readObject();
        byteArrayInputStream.close();
        objectInputStream.close();
        return request;
    }

    private byte[] serialize(Response response) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(response);
        byte[] buffer = byteArrayOutputStream.toByteArray();
        objectOutputStream.flush();
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        objectOutputStream.close();
        return buffer;
    }
}