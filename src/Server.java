// 1. Клиент посылает через сервер сообщение другому клиенту.

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private Map<String, ObjectOutputStream> clients = new HashMap<>(); // создание мапы для отображения имени клиента на соответствующий поток вывода

    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Сервер запущен на порту " + port);

            while (true) {
                Socket socket = serverSocket.accept(); // принятие входящего соединения от клиента, возвращает новый сокет для общения с клиентом
                System.out.println("Новое соединение: " + socket);

                ObjectInputStream input = new ObjectInputStream(socket.getInputStream()); // создание потока ввода для чтения объектов из сокета
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream()); // создание потока вывода для записи объектов в сокет

                String clientName = (String) input.readObject(); // чтение имени клиента из входящего потока
                clients.put(clientName, output); // добавление клиента и его потока вывода в мапу клиентов
                System.out.println("Клиент " + clientName + " присоединился");

                Thread handler = new ClientHandler(socket, input, output, clientName); // создание нового потока для обработки сообщений от клиента
                handler.start();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private ObjectInputStream input;
        private String clientName;

        public ClientHandler(Socket socket, ObjectInputStream input, ObjectOutputStream output, String clientName) {
            this.socket = socket;
            this.input = input;
            this.clientName = clientName;
        }

        public void run() {
            try {
                while (true) {
                    String recipient = (String) input.readObject(); // чтение имени получателя сообщения
                    String message = (String) input.readObject(); // чтение текста сообщения
                    System.out.println("Получено сообщение от " + clientName + " для " + recipient + ": " + message);

                    ObjectOutputStream recipientOutput = clients.get(recipient); // получение потока вывода для клиента-получателя из мапы клиентов
                    if (recipientOutput != null) {
                        recipientOutput.writeObject(clientName + ": " + message); // потока вывода для клиента-получателя
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start(12345); // Порт сервера
    }
}
