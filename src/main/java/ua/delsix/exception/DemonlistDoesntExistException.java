package ua.delsix.exception;

public class DemonlistDoesntExistException extends Exception {
    public DemonlistDoesntExistException(String message) {
        super(message);
    }

    public DemonlistDoesntExistException() {
    }
}
