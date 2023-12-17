import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345); // Адрес и порт сервера
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream()); // создание потока вывода для отправки объектов на сервер
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream()); // создание потока ввода для чтения объектов от сервера

            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите ваше имя: ");
            String clientName = scanner.nextLine();
            output.writeObject(clientName); // отправка имени клиента на сервер

            while (true) {
                System.out.print("Кому вы хотите отправить сообщение: ");
                String recipient = scanner.nextLine();
                output.writeObject(recipient);
                System.out.print("Введите сообщение: ");
                String message = scanner.nextLine();
                output.writeObject(message);

                String incomingMessage = (String) input.readObject();
                System.out.println("Получено сообщение: " + incomingMessage);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}