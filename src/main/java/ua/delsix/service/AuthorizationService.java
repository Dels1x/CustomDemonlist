package ua.delsix.service;

import org.springframework.stereotype.Service;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.entity.User;

@Service
public class AuthorizationService {
    public void verifyOwnershipOfTheDemonlist(Demonlist demonlist, User user) throws AuthorizationException {
        if (demonlist == null || !demonlist.getUser().equals(user)) {
            throw new AuthorizationException("You are not authorized to perform actions with the desired demonlist");
        }
    }
}
