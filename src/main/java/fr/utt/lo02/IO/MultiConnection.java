package fr.utt.lo02.IO;
import java.io.*;

public class MultiConnection {
    public static void main(String[] args) {
        // ask if we want to host or join
        System.out.println("Do you want to host or join?");
        System.out.println("1. Host");
        System.out.println("2. Join");
        System.out.print("Choice: ");
        int choice = 0;
        try {
            choice = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
            if (choice < 1 || choice > 2) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice.");
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.print("Enter port number: ");
        int port = 0;
        try {
            port = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
            if (port < 0 || port > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            port = 1234;
            System.out.println("Invalid port number. Defaulting to 1234.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (choice == 1) {
            Server server = new Server(port, 2);
            server.start();
        }
        else {
            String ip;
            System.out.print("Enter IP address: ");
            try {
                ip = new BufferedReader(new InputStreamReader(System.in)).readLine();
            } catch (IOException e) {
                ip = "localhost";
                System.out.println("Invalid IP address. Defaulting to localhost.");
            }
            Client client = new Client(ip, port);
            client.start();
        }

        return;
    }
}
