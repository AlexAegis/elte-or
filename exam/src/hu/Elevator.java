package hu;

import hu.connection.Connection;
import hu.connection.ControlHandler;
import hu.connection.Handler;
import hu.connection.ResidentHandler;
import hu.model.Direction;
import hu.model.Story;

import java.net.ServerSocket;
import java.util.*;

public class Elevator implements Runnable {

    public static final int RESIDENT_PORT = 13578;
    public static final int LIFT_CONTROL_PORT = 13579;

    public List<Connection> connections = new ArrayList<>();
    public List<ServerSocket> sockets = new ArrayList<>();

    public final Handler controlHandler;
    public final Handler residentHandler;

    public Integer id = 1;
    public Integer capacity;
    public Integer position = 0;
    public List<Integer> content = new ArrayList<>();
    public HashMap<Integer, ArrayDeque<Integer>> requests = new HashMap<>();
    public Boolean active = Boolean.TRUE;

    public Elevator() {
         controlHandler = new ControlHandler(this);
         residentHandler = new ResidentHandler(this);
    }

    public static void main(String[] args) {
        Elevator elevator = new Elevator();
        elevator.run();
    }

    @Override
    public void run() {
        try (
                ServerSocket controlSocket = new ServerSocket(LIFT_CONTROL_PORT);
                ServerSocket residentSocket = new ServerSocket(RESIDENT_PORT)
        ) {
            sockets.add(controlSocket);
            sockets.add(residentSocket);
            if(active) {
                Connection controlConnection = new Connection(controlSocket.accept(), controlHandler);
                controlConnection.run();
                connections.add(controlConnection);
                Connection residentConnection = new Connection(residentSocket.accept(), residentHandler);
                residentConnection.run();
                connections.add(residentConnection);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer move(Direction dir) {
        position += dir.getDelta();
        return position;
    }

    public ArrayDeque<Integer> currentStory() {
        if (!requests.containsKey(position)) {
            requests.put(position, new ArrayDeque<>());
        }
        return requests.get(position);
    }

    public void close() {
        System.out.println("[Goodbye!]");
        active = false;
        for (Connection connection : connections) {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (ServerSocket socket : sockets) {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        connections.clear();
        sockets.clear();
    }
}
