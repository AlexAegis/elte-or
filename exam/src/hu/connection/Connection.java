package hu.connection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Connection implements AutoCloseable {

    private Socket clientSocket;
    private PrintWriter out;
    private Scanner in;
    private Thread incomingThread;
    private Thread outgoingThread;
    private boolean active = true;

    private Handler handler;

    public Connection(Socket clientSocket, Handler handler) throws IOException {
        this.clientSocket = clientSocket;
        this.out = new PrintWriter(clientSocket.getOutputStream());
        this.in = new Scanner(clientSocket.getInputStream());
        this.handler = handler;
    }

    @Override
    public void close() throws Exception {
        out.println("exit");
        out.flush();
        active = false;
        out.close();
        in.close();
        clientSocket.close();
    }


    public void run() {

        incomingThread = new Thread(() -> {
            System.out.println("Starting client hu.connection");
            // mgr.add(this);
            while (active && in.hasNextLine()) {
                String input = in.nextLine();
                System.out.println("Got input from client:" + input);
                try {

                    String response = this.handler.handle(input);
                    out.println(response);
                    out.flush();
                } catch (Exception e) {
                    // survive invalid input
                    e.printStackTrace();
                }
            }
            System.out.println("hu.Client disconnected");
            System.out.println("hu.Client threads done.");

        });
        incomingThread.start();
    }
}
