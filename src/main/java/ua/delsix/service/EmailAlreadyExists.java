package ua.delsix.service;

public class EmailAlreadyExists extends Throwable {
    public EmailAlreadyExists(String s) {
        super(s);
    }
}
