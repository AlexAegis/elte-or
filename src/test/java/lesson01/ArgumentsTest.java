package lesson01;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lesson01.Arguments;

public class ArgumentsTest {

    @Test
    public void test() {
        Arguments.ResultPair resultPair = new Arguments().task("alma", "3", "körte", "6");
        System.out.println("ASd");
        assertEquals(resultPair.count, 4);
        assertEquals(resultPair.sum, 9);
    }
}
