package ua.delsix.exception;

public class DemonDoesntExistException extends RuntimeException {
    public DemonDoesntExistException(String message) {
        super(message);
    }
}
