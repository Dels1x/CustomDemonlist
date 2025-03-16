package ua.delsix.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// messages for API calls
public class ResponseUtil {
    private ResponseUtil() {
        throw new AssertionError("Should not be instantiated");
    }

    public static ResponseEntity<String> demonlistDoesntExistMessage() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provided demonlist doesn't exist");
    }

    public static ResponseEntity<String> demonDoesntExistMessage() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provided demon doesn't exist");
    }

    public static ResponseEntity<String> authorizationExceptionMessage(String message){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
    }

    public static ResponseEntity<String> userDoesntExistMessage(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    public static ResponseEntity<String> notFoundMessage(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    public static ResponseEntity<String> moreDemonlistsThanAllowed() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have exceeded the allowed amount of demonlists");
    }

    public static ResponseEntity<String> illegalArgument(String message) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(message);
    }

    public static ResponseEntity<String> invalidName(String message) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(message);
    }
}
