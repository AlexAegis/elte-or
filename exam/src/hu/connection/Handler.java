package hu.connection;

public interface Handler {
    String handle(String request);

    default void log(String string) {
        System.out.println("[" + handlerName() + "] " + string);
    }

    String handlerName();
}
