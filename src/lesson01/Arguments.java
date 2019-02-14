package lesson01;

import java.util.Arrays;

public class Arguments {
    public static void main(String[] args) {
        System.out.println(args.length);
        System.out.println(Arrays.stream(args).filter(s -> s.chars().allMatch(Character::isDigit))
                .mapToInt(Integer::parseInt).sum());
    }
}
