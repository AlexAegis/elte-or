package hu.connection;

import hu.Elevator;
import hu.model.Direction;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ControlHandler implements Handler {

    private Elevator elevator;

    public ControlHandler(Elevator elevator) {
        this.elevator = elevator;
    }

    @Override
    public String handle(String request) {

        if(elevator.capacity == null) {
            // First request is the capacity of the elevator
            if(request != null && request.chars().allMatch(Character::isDigit)) {
                elevator.capacity = Integer.parseInt(request);
                log("Got capacity: " + elevator.capacity);
            }

            return "Elevator initialized with capacity: " + elevator.capacity;
        }

        var out = 0;
        var in = new ArrayList<Integer>();
        if(request != null && (request.equals("up") || request.equals("down"))) {
            var dir = Direction.valueOf(request.toUpperCase());
            log("got dir: " + dir.getDelta());

            var pos = elevator.move(dir);

            log("pos after move: " + pos);

            var story = elevator.currentStory();
            var before = elevator.content.size();
            elevator.content.removeIf(e -> e.equals(elevator.position)); // Get out!
            out = before - elevator.content.size();
            log("Story deque: " + story.toString() + " Story requests size: " + story.size() + "  elevator.capacity: " +  elevator.capacity);

            while(story.size() > 0 && elevator.content.size() < elevator.capacity) {
                var pops = story.pop();
                System.out.println("Cunt wanna get in: " + pops);
                in.add(pops);
                elevator.content.add(pops);
            }
            log("Elevator content: " + elevator.content);
        }

        if(elevator.position == 0 && elevator.content.isEmpty()) {
            // We're in endgame now.
            elevator.close();
        }



        return elevator.id + ". lift, "
                + elevator.position + ". szint, "
                + out + " ki, [" + in.stream().map(Object::toString).collect(Collectors.joining(",")) + "] be";
    }

    @Override
    public String handlerName() {
        return "Control";
    }
}
