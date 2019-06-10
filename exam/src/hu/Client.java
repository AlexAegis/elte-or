package hu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    private static int id = 1;

    public static final String LOCALHOST = "127.0.0.1";
    private int port;
    private static boolean running = true;

    public Client(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (
                Socket socket = new Socket(LOCALHOST, port);
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream());
                Scanner in =
                        new Scanner(
                                new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ) {
            Thread tToServer = new Thread(() -> {
                try {
                    while (running) {
                        String read =stdIn.readLine();
                        if(read.equals("exit")) {
                            running = false;
                            break;
                        }
                        String msg = read;
                        out.println(msg);
                        out.flush();
                        System.out.println("Message sent: " + msg);
                        id++;
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            });

            Thread tFromServer = new Thread(() -> {
                while (running && in.hasNextLine()) {
                    var response = in.nextLine();
                    System.out.println("Message received: " +response);
                    if("exit".equals(response)) {
                        running = false;
                        break;
                    }
                }
            });

            tFromServer.start();
            tToServer.start();

            tFromServer.join();
            tToServer.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
