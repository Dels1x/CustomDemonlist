package ua.delsix.exception;

public class SamePasswordReset extends Exception {
    public SamePasswordReset(String message) {
        super(message);
    }

    public SamePasswordReset() {
    }
}
