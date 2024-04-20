package ua.delsix.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static ResponseEntity<String> demonlistDoesntExistMessage() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provided demonlist doesn't exist");
    }

    public static ResponseEntity<String> authorizationExceptionMessage(String s){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(s);
    }
}
