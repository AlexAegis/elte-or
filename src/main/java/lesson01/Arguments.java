package lesson01;

import java.util.Arrays;
import java.util.Objects;

public class Arguments {
	public static void main(String... args) {
		System.out.println(new Arguments().task("alma", "3", "körte", "6").toString());
	}

	public ResultPair task(String... args) {
		return new ResultPair(args.length, Arrays.stream(args).filter(s -> s.chars().allMatch(Character::isDigit))
				.mapToInt(Integer::parseInt).sum());
	}

	public static class ResultPair {
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

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			var that = (ResultPair) o;
			return count == that.count && sum == that.sum;
		}

		@Override
		public int hashCode() {
			return Objects.hash(count, sum);
		}
	}

}
