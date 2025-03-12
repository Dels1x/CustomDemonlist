package ua.delsix.exception;

public class MoreDemonlistsThanAllowedException extends Exception {
    public MoreDemonlistsThanAllowedException(String message) {
        super(message);
    }

    public MoreDemonlistsThanAllowedException() {
    }
}
