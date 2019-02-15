package lesson01;

import java.util.Arrays;

public class Arguments {
    public static void main(String... args) {
        System.out.println(new Arguments().task("alma", "3", "körte", "6").toString());
    }

    ResultPair task(String... args) {
        return new ResultPair(args.length, Arrays.stream(args).filter(s -> s.chars().allMatch(Character::isDigit))
                .mapToInt(Integer::parseInt).sum());
    }

    class ResultPair {
        int count;
        int sum;

        public ResultPair(int count, int sum) {
            this.count = count;
            this.sum = sum;
        }

        @Override
        public String toString() {
            return "ResultPair{" + "count=" + count + ", sum=" + sum + '}';
        }
    }

}
