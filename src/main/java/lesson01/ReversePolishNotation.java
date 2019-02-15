package lesson01;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;

public class ReversePolishNotation {

	public static void main(String[] args) {
		System.out.println(eval("15 7 1 1 + - / 3 * 2 1 1 + + -")); // 5
	}

	static Map<String, BiFunction<Integer, Integer, Integer>> op = Map.ofEntries(
			new SimpleImmutableEntry<String, BiFunction<Integer, Integer, Integer>>("+", (op1, op2) -> op2 + op1),
			new SimpleImmutableEntry<String, BiFunction<Integer, Integer, Integer>>("-", (op1, op2) -> op2 - op1),
			new SimpleImmutableEntry<String, BiFunction<Integer, Integer, Integer>>("*", (op1, op2) -> op2 * op1),
			new SimpleImmutableEntry<String, BiFunction<Integer, Integer, Integer>>("/", (op1, op2) -> op2 / op1),
			new SimpleImmutableEntry<String, BiFunction<Integer, Integer, Integer>>("%", (op1, op2) -> op2 % op1));

	static Integer eval(String rpn) {
		return Arrays.stream(rpn.split(" ")).collect(Stack<Integer>::new, (acc, next) -> {
			if (op.containsKey(next)) {
				acc.push(op.get(next).apply(acc.pop(), acc.pop()));
			} else if (next.chars().allMatch(Character::isDigit)) {
				acc.push(Integer.parseInt(next));
			}
			System.out.printf("Next: %s, Acc: %s\n", next, acc.toString());
		}, (a, b) -> {
		}).pop();
	}

}
