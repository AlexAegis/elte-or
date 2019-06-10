package hu;

public class ControlClient {

    public static void main(String[] args) {
        Client client = new Client(Elevator.LIFT_CONTROL_PORT);
        client.run();
    }
}
