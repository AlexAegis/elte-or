package hu.connection;

import hu.Elevator;

import java.util.*;
import java.util.stream.Collectors;

public class ResidentHandler implements Handler{

    private Elevator elevator;

    public ResidentHandler(Elevator elevator) {
        this.elevator = elevator;
    }

    @Override
    public String handle(String request) {

        if(request != null && Arrays
                .stream(request.split(" "))
                .allMatch(numstr -> numstr.chars().allMatch(Character::isDigit))) {
            var reqArray = request.split(" ");
           log("rekk len: " + reqArray.length + " content: " + String.join(" ", reqArray));
            if(reqArray.length > 0) {
                var story = Integer.parseInt(reqArray[0]);
                var reqs = Arrays.asList(reqArray)
                        .subList(1, reqArray.length)
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                log("story: " + story);
                log("reqs: " + reqs.toString());

                addRequest(story, reqs);
            }
        }
        return "got: " + request;
    }

    @Override
    public String handlerName() {
        return "Resident";
    }

    private void addRequest(Integer story, List<Integer> targets) {
        if (!elevator.requests.containsKey(story)) {
            elevator.requests.put(story, new ArrayDeque<>());
        }
        elevator.requests.get(story).addAll(targets);
    }

}
