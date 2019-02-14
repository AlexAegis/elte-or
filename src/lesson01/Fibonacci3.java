package lesson01;

public class Fibonacci3 {
    public static void main(String[] args) {
        System.out.println(fib(5));
    }

    static int fib(int n) {

        int prevbef = 0;
        int prev =1;
        int sum = 0;
        for (int i = 1; i <= n ; i++) {
            sum += prev + prevbef;
            prevbef = prev;
            prev = i;
        }
        return sum;
    }
}
