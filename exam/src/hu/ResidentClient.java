package hu;

public class ResidentClient {

    public static void main(String[] args) {
        Client client = new Client(Elevator.RESIDENT_PORT);
        client.run();
    }
}
