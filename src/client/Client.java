package client;

import common.CommandProcessor;
import common.Request;
import common.Response;
import common.commands.CommandType;
import common.commands.ExecuteScriptCommand;


import java.io.*;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;


public class Client {
    private String host;
    private int port;
    private ObjectOutputStream objectSender;
    private ObjectInputStream objectReader;
    private DatagramChannel channel;
    private Selector selector;
    private SocketAddress address;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(16384);
    private Scanner scanner;
    private CommandProcessor cm;


    public Client(String host, int port, CommandProcessor cm) {
        this.host = host;
        this.port = port;
        this.cm = cm;
    }

    public void start() {
        try {
            boolean isRunning = true;
            while (isRunning) {
                connect();
                isRunning = exchangeDataWithServer();
            }
            if (channel != null) channel.close();
            System.out.println("Работа клиента завершена.");
        } catch (IOException exception) {
            System.out.println("Произошла ошибка при попытке завершить соединение с сервером!");
        }
    }

    public void connect() {
        boolean tryingToConnect = true;
        do {
            try {
                channel = DatagramChannel.open();
                address = new InetSocketAddress("localhost", port);
                channel.connect(address);
                channel.configureBlocking(false);
                selector = Selector.open();
                channel.register(selector, SelectionKey.OP_WRITE);
                System.out.println("Подключение установлено.");
                tryingToConnect = false;
                //objectSender = new ObjectOutputStream(socketChannel.socket().getOutputStream());
                //objectReader = new ObjectInputStream(socketChannel.socket().getInputStream());
                System.out.println("Готов к передаче данных.");
            } catch (IllegalArgumentException e) {
                System.out.println("Проверьте правильность введенного адреса.");
            } catch (IOException e) {
                System.out.println("Ошибка при соединении с сервером.");
            }
        } while (tryingToConnect);


    }

    public boolean exchangeDataWithServer() {
        Request request = null;
        Response response = null;
        do {
            try {
                String[] commandSplit = cm.readCommand();
                request = cm.generateRequest(commandSplit); //request = null
                if (request.isEmpty()) continue;
//                if (request.getCommand().getCommandType().equals(CommandType.EXECUTE_SCRIPT)) {
//                    sendRequest(request);
//                    continue;
//                }
      //          sendRequest(request);
                send(request);

                byteBuffer.clear();
                response = receive();
                if (response == null) continue;
                System.out.println(response.getResponseInfo());
            } catch (InvalidClassException ex) {
                System.out.println("Произошла ошибка при отправке данных на сервер!");
            } catch (NotSerializableException exception){
                System.out.println("Ошибка сереализации");
            }  catch (ClassNotFoundException exception) {
                System.out.println("Произошла ошибка при чтении полученных данных!");
            } catch (IOException exception) {
                System.out.println("Соединение с сервером разорваноk,k,j!");
                exception.printStackTrace();
                try {
                    connect();
                } catch (Exception e) {
                    System.out.println("Ошибка передачи данных. Команда не была доставлена на сервер.");
                }
            }
        } while (request.isEmpty() || !request.getCommand().getCommandType().equals(CommandType.EXIT) );
        return false;
    }

//    public void sendRequest(Request request) throws IOException, ClassNotFoundException {
//
//        if (request.getCommand().getCommandType().equals(CommandType.EXECUTE_SCRIPT)) {
//            ExecuteScriptCommand execScr = (ExecuteScriptCommand) request.getCommand();
//            ArrayList<Request> script = cm.executeScript(execScr.getFilename());
//            for (Request cmd : script) {
//                if (cmd == null || cmd.isEmpty()) continue;
//                objectSender.writeObject(cmd);
//                getResponse();
//            }
//        }
//        else {
//            objectSender.writeObject(request);
//        }
//    }

    public void getResponse() throws IOException, ClassNotFoundException {
        Response response;
        response = (Response) objectReader.readObject();
        if (!response.isEmpty()) System.out.println(response.getResponseInfo());
    }

    private void makeByteBufferToRequest(Request request) throws IOException {
        byteBuffer.put(serialize(request));
        byteBuffer.flip();
    }

    private byte[] serialize(Request request) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(request);
        byte[] buffer = byteArrayOutputStream.toByteArray();
        objectOutputStream.flush();
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        objectOutputStream.close();
        return buffer;
    }

    private Response deserialize() throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBuffer.array());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        try {
            Response response = (Response) objectInputStream.readObject();
            byteArrayInputStream.close();
            objectInputStream.close();
            byteBuffer.clear();
            return response;
        } catch (ClassCastException e ){
            e.printStackTrace();
        }
        return new Response("lala");
    }

    private void send(Request request) throws IOException, ClassNotFoundException {
        makeByteBufferToRequest(request);

        DatagramChannel channel = null;
        while (channel == null) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            for (SelectionKey key : selectionKeys) {
                if (key.isWritable()) {
                    channel = (DatagramChannel) key.channel();
                    if (request.getCommand().getCommandType().equals(CommandType.EXECUTE_SCRIPT)) {
                        System.out.println("aaaaa");
                        ExecuteScriptCommand execScr = (ExecuteScriptCommand) request.getCommand();
                        ArrayList<Request> script = cm.executeScript(execScr.getFilename());
                        ByteBuffer execScrBuffer = ByteBuffer.allocate(16384);
                        System.out.println("scrsize " + script.size());
                        for (Request cmd : script) {
                            System.out.println("повтор!");
                            if (cmd == null || cmd.isEmpty()) continue;
                            execScrBuffer.put(serialize(cmd));
                            execScrBuffer.flip();
                            channel.write(execScrBuffer);
                            channel.register(selector, SelectionKey.OP_READ);
                            //System.out.println(receive().getResponseInfo());
                            channel.register(selector, SelectionKey.OP_WRITE);
                            byteBuffer.clear();
                            execScrBuffer.clear();
                        }
                    }

                    else {
                        channel.write(byteBuffer);
                    }
                    channel.register(selector, SelectionKey.OP_READ);
                    break;
                }
            }
        }
        byteBuffer.clear();
    }

    private Response receive() throws IOException, ClassNotFoundException {
        DatagramChannel channel = null;
        while (channel == null) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            for (SelectionKey key : selectionKeys) {
                if (key.isReadable()) {
                    //byteBuffer.clear();
                    channel = (DatagramChannel) key.channel();
                    channel.read(byteBuffer);
                    byteBuffer.flip();
                    channel.register(selector, SelectionKey.OP_WRITE);
                   // byteBuffer.clear();
                    break;
                }
            }
        }
        return deserialize();
    }
}