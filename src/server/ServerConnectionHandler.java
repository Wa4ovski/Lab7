package server;

import common.Request;
import common.Response;

import javax.print.DocFlavor;
import java.io.*;
import java.net.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ServerConnectionHandler {
    private int port;
    private ServerSocket serverSocket;
    private ServerRequestHandler requestHandler;
    private int timeout = 300;
    private ServerInputHandler serverInputHandler;
    private Socket clientSocket;
    private ByteBuffer buffer = ByteBuffer.allocate(2408);

    public ServerConnectionHandler(int port, ServerRequestHandler requestHandler, ServerInputHandler serverInputHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
        this.serverInputHandler = serverInputHandler;
    }

    public void start()  {
        initializeServerSocket();
        boolean listening = true;
        System.out.println("Запуск сервера...");
        serverInputHandler.start();
        while (listening) {
            try {
                receiveClientSocket();
                System.out.println("Соединение с клиентом установлено.");
                listening = processRequest();
            } catch (SocketException e) {
                System.out.println("Потеряна связь с клиентом.");
            } catch (IOException e) {
                System.out.println("Ошибка подключения к серверу.");
            }
        }
    }

    public void startCh() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(port));
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            Response currentResponse = null;

            while (true) {
                int num = selector.select();
                if (num == 0) continue;
                Set<SelectionKey> keys = selector.selectedKeys();

                for (SelectionKey key : keys) {
                    if (key.isAcceptable()) {
                        SocketChannel clientChannel = serverSocketChannel.accept();
                        if (clientChannel == null) continue;
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);// read
                    } else {
                        if (key.isReadable()) {
                            SocketChannel client = null;
                            client = (SocketChannel) key.channel();
                            client.read(buffer);
                            buffer.flip();
                            client.register(selector, SelectionKey.OP_READ);
                            byte[] arr = buffer.array();
                            Request r = deserialize(arr);
                            currentResponse = requestHandler.processClientRequest(r);
                            client.register(selector, SelectionKey.OP_WRITE);
                            buffer.clear();
                            break;
                        }
                        if (key.isWritable()) {
                            SocketChannel clientResponse = null;
                            clientResponse = (SocketChannel) key.channel();
                            byte[] responseBytes = serialize(currentResponse);
                            buffer.put(responseBytes);
                            buffer.flip();
                            clientResponse.write(buffer);
                            clientResponse.register(selector, SelectionKey.OP_READ);
                            buffer.clear();
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }


    }


    private void initializeServerSocket() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(timeout * 1000);
            System.out.println("Сокет открыт для подключения");
        } catch (SocketException e) {
            System.out.println("error");
        } catch (IOException e) {
            System.out.println("Ошибка при открытии порта: " + port);
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный порт! Проверьте правильность введенных данных.");
        }
    }
    public void receiveClientSocket() throws IOException {
        try {
            System.out.println("Ожидаю подключения клиента на порт: " + port);
            clientSocket = serverSocket.accept();
        } catch (SocketTimeoutException e) {
            System.out.println("Превышено время ожидания подключения.");
            throw new SocketTimeoutException();
        } catch (IOException e) {
            System.out.println("Непредвиденная ошибка соединения");
            throw new IOException();
        }
    }

    private boolean processRequest() {
        Request request = null;
        Response response = null;
        try {
            //ObjectOutputStream clientWriter = new ObjectOutputStream(clientSocket.getOutputStream());
            do {
                ObjectInputStream clientReader = new ObjectInputStream(clientSocket.getInputStream());
                request = (Request) clientReader.readObject();
                // request = getRequest();
                if (request == null) continue;
                response = requestHandler.processClientRequest(request);
                sendResponse(response);
                //clientWriter.writeObject(response);
                //clientWriter.flush();
            } while (true); //need to do exit command and wait for it
        } catch (ClassNotFoundException e) {
            System.out.println("Ошибка чтения запроса. Проверьте целостность серверных файлов.");
        } catch (SocketException e) {
            System.out.println("Потеряно соединение с клиентом.\n");
        } catch (IOException e) {
            //System.out.println("Непредвиденная ошибка обработки запроса.");
            e.printStackTrace();
        }
        return true;
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
    private void sendResponse(Response response) throws IOException {
        byte[] sendBuffer = serialize(response);
        OutputStream out = clientSocket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);
        dos.write(sendBuffer, 0, sendBuffer.length);
    }

    private Request getRequest() throws IOException, ClassNotFoundException {
        byte[] getBuffer = new byte[65536];
        InputStream in = clientSocket.getInputStream();
        DataInputStream dis = new DataInputStream(in);
        dis.readFully(getBuffer);
        return deserialize(getBuffer);
    }

    private Request deserialize(byte[] buffer) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Request request = (Request) objectInputStream.readObject();
        byteArrayInputStream.close();
        objectInputStream.close();
        return request;
    }

}
