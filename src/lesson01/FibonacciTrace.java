package lesson01;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FibonacciTrace {
    public static void main(String[] args) {
        System.out.println(fib(4).trace.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }

    private static FibAcc fib(int n) {
        return IntStream.range(0, n).collect(FibAcc::new, FibAcc::shift, (a, b) -> {
        });
    }

    static class FibAcc {

        List<Integer> trace = new ArrayList<>();
        private int prev = 1;

        private int prevBef;
        int sum;

        void shift(Integer i) {

            sum += prev + prevBef;
            prevBef = prev;
            prev = i;
            trace.add(sum);
        }
    }

}
