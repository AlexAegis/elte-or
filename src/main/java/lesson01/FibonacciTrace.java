package lesson01;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FibonacciTrace {
    public static void main(String[] args) {
        System.out.println(fib(40).trace.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }

    private static Fib fib(int n) {
        return IntStream.range(0, n).collect(Fib::new, Fib::shift, (a, b) -> {
        });
    }

    static class Fib {
        List<Integer> trace = new ArrayList<>();
        private int t1 = 1;
        private int t2;

        void shift(Integer i) {
            int sum = t1 + t2;
            t1 = t2;
            t2 = sum;
            trace.add(t1);
        }
    }

}