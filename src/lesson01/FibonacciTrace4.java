package lesson01;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FibonacciTrace4 {

    public static void main(String[] args) {
        System.out.println(fib(5).stream().map(Object::toString).collect(Collectors.joining(", ")));
    }

    static List<Integer> fib(int n) {
        List<Integer> trace = new ArrayList<>();
        int prevbef = 0;
        int prev =1;
        int sum = 0;
        trace.add(sum);
        for (int i = 1; i <= n ; i++) {
            sum += prev + prevbef;
            trace.add(sum);
            prevbef = prev;
            prev = i;
        }
        return trace;
    }
}
