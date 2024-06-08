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

    public void verifyUserAuthorization(User user1, User user2) throws AuthorizationException {
        if ((!user1.equals(user2))) {
            throw new AuthorizationException("You are not authorized to perform actions with the desired user");
        }
    }

    public boolean isAuthorized(User user1, User user2) {
        return user1.equals(user2);
    }
}
