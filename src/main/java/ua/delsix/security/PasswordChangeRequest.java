package ua.delsix.security;

public class PasswordChangeRequest {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public PasswordChangeRequest(String password) {
        this.password = password;
    }

    public PasswordChangeRequest() {
    }
}
