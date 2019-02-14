package lesson01;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class Arguments {
    public static void main(String... args) {
        System.out.println(new Arguments().task(args).toString());
    }

    private ResultPair task(String... args) {
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

    @Test
    public void test() {
        ResultPair resultPair = task("alma", "3", "körte", "6");
        Assert.assertEquals(resultPair.count, 4);
        Assert.assertEquals(resultPair.sum, 9);
    }
}
