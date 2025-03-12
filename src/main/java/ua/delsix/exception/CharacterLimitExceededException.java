package ua.delsix.exception;

public class CharacterLimitExceededException extends Exception {
    public CharacterLimitExceededException(String message) {
        super(message);
    }
}
