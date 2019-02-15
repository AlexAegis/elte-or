package lesson01;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class ReversePolishNotation {
	public static void main(String[] args) {
		System.out.println(RPN.eval("15 7 1 1 + - / 3 * 2 1 1 + + -")); // 5
	}

	static class RPN {
		Stack<Integer> stack = new Stack<>();

		static List<String> operators = Arrays.asList("+", "-", "−", "*", "×", "/", "÷");

		void apply(String ex) {
			Integer op2 = stack.pop();
			Integer op1 = stack.pop();
			switch (ex) {
			case "+":
				stack.push(op1 + op2);
				break;
			case "-":
			case "−":
				stack.push(op1 - op2);
				break;
			case "*":
			case "×":
				stack.push(op1 * op2);
				break;
			case "/":
			case "÷":
				stack.push(op1 / op2);
				break;
			}
		}

		void read(String input) {
			if (operators.contains(input)) {
				apply(input);
			} else if (input.chars().allMatch(Character::isDigit)) {
				stack.push(Integer.parseInt(input));
			}
			System.out.println("input: " + input + " stack: " + stack.toString());
		}

		static int eval(String rpn) {
			return Arrays.stream(rpn.split(" ")).collect(RPN::new, RPN::read, (a, b) -> {
			}).stack.pop();
		}

	}

}
