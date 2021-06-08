package client;

import common.CommandProcessor;
import common.Request;
import common.Response;
import common.commands.AuthCommand;
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
    private final static int MAX_RECONNECTION_ATTEMPTS = 5;
    private final static int RECONNECTION_TIMEOUT_IN_SECONDS = 5;
    private String host;
    private int port;
    private ObjectOutputStream objectSender;
    private ObjectInputStream objectReader;
    private DatagramChannel channel;
    private Selector selector;
    private SocketAddress address;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(16384);
    private Scanner scanner;
    private CommandProcessor cp;

    private String serverToken;
    private String username;
    private boolean isAuthed = false;
   // private ObjectInputStream ois;
    //private ObjectOutputStream oos;



    public Client(String host, int port, CommandProcessor cp) {
        this.host = host;
        this.port = port;
        this.cp = cp;
    }

    public void start() {
        connect();
        System.out.println("Для выполнения команд требуется авторизация: auth reg/login <username>");
        exchangeDataWithServer();
        System.out.println("Клиент завершил свою работу.");
    }

    public boolean connect() {
        int reconnectionAttempt = 0;
        do {
            try {
                if (reconnectionAttempt > 0) {
                    Thread.sleep(RECONNECTION_TIMEOUT_IN_SECONDS * 1000);
                    System.out.printf("Пытаюсь переподключиться (попытка %d)\n", reconnectionAttempt);
                }
                channel = DatagramChannel.open();
                address = new InetSocketAddress("localhost", port);
                channel.connect(address);
                channel.configureBlocking(false);
                selector = Selector.open();
                channel.register(selector, SelectionKey.OP_WRITE);
                System.out.println("Подключение установлено.");
                System.out.println("Готов к передаче данных.");
                reconnectionAttempt = 0;
                return true;
                //objectSender = new ObjectOutputStream(socketChannel.socket().getOutputStream());
                //objectReader = new ObjectInputStream(socketChannel.socket().getInputStream());

            }catch (IOException e) {
                System.out.println("Ошибка подключения.");
                reconnectionAttempt++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (IllegalArgumentException e) {
                System.out.println("Проверьте правильность введенного адреса.");
            }
        } while (reconnectionAttempt <= MAX_RECONNECTION_ATTEMPTS);
        return false;
    }

    public void exchangeDataWithServer() {
        Request request = null;
        Response response = null;
        boolean latestRequestIsDelivered = true;

        while(true) {
            try {
                if (!latestRequestIsDelivered) {
                    request.addToken(serverToken);
                    if (request.isEmpty()) continue;
                    send(request);
                    //getResponse();
                    //response = getResponse();
                    byteBuffer.clear();
                    response = receive();//TODO может get()?
                    System.out.println(response.getResponseInfo());
                    latestRequestIsDelivered = true;
                }
//                String[] commandSplit = cp.readCommand();
//                request = cp.generateRequest(commandSplit); //request = null
//                if (request.isEmpty()) continue;
////                if (request.getCommand().getCommandType().equals(CommandType.EXECUTE_SCRIPT)) {
////                    sendRequest(request);
////                    continue;
////                }
//      //          sendRequest(request);
//
//                send(request);
//
//                byteBuffer.clear();
//                response = receive();
//                if (response == null) continue;
//                System.out.println(response.getResponseInfo());

                String[] commandSplit = cp.readCommand();
                request = cp.generateRequest(commandSplit);
                if (request.isEmpty() || request == null) continue;
                if (request.getCommand().getCommandType().equals(CommandType.EXIT)) break;
                if (isAuthed) {
                    request.addToken(serverToken);
                    request.addInitiator(username);
                }
//                if (request.getCommand().getCommandType().equals(CommandType.EXECUTE_SCRIPT)) {
//                    processExecuteScriptRequest(request);
//                    continue;
//                }
                send(request);
                byteBuffer.clear();
                response = receive(); // TODO getResponse();
                if (request.getCommand().getCommandType().equals(CommandType.AUTH)) {
                    AuthCommand auth = (AuthCommand) request.getCommand();
                    if (auth.getAuthType().equals(AuthCommand.AuthType.LOGIN) && response.isOK()) {
                        serverToken = response.getServerToken();
                        username = auth.getUsername();
                         System.out.println("Получен токен: " + serverToken);
                        isAuthed = true;
                    }
                }
                System.out.println(response.getResponseInfo());
            } catch (IOException e) {
                latestRequestIsDelivered = false;
                isAuthed = false;
                System.out.println("Потеряно соединение с сервером. Будет выполнена попытка переподключения.");
                boolean reconnectionSuccess = connect();
                if (reconnectionSuccess) {
                    // System.out.println("Требуется повторная авторизация: auth login <username>.");
                    continue;
                }
                break;
            } catch (ClassNotFoundException e) {
                System.out.println("Произошла ошибка при чтении полученных данных!");
                e.printStackTrace();
            }
        }
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

//    public void getAndPrintResponse() throws IOException, ClassNotFoundException {
//        Response response;
//        response = (Response) objectReader.readObject();
//        if (!response.isEmpty()) System.out.println(response.getResponseInfo());
//    }

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
                        ArrayList<Request> script = cp.executeScript(execScr.getFilename());
                        ByteBuffer execScrBuffer = ByteBuffer.allocate(16384);
                        System.out.println("scrsize " + script.size());
                        for (Request cmd : script) {

                            System.out.println("повтор!");
                            if (cmd == null || cmd.isEmpty()) continue;
                            execScrBuffer.put(serialize(cmd));
                            execScrBuffer.flip();
                            channel.write(execScrBuffer);
                            channel.register(selector, SelectionKey.OP_READ);
                            //System.out.println("sxs");
                            //System.out.println(receive().getResponseInfo());
                            byteBuffer.clear();
                            channel.read(byteBuffer);
                            byteBuffer.flip();
                          //  getResponse();
                            System.out.println(deserialize().getResponseInfo());
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

    public Response getResponse() throws IOException, ClassNotFoundException {
        Response response = (Response) objectReader.readObject();
        return response;
    }
}