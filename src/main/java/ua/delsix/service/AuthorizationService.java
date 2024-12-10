package ua.delsix.service;

import org.springframework.stereotype.Service;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.entity.Person;

@Service
public class AuthorizationService {
    public void verifyOwnershipOfTheDemonlist(Demonlist demonlist, Person person) throws AuthorizationException {
        if (demonlist == null || !demonlist.getPerson().equals(person)) {
            throw new AuthorizationException("You are not authorized to perform actions with the desired demonlist");
        }
    }

    public void verifyUserAuthorization(Person person1, Person person2) throws AuthorizationException {
        if ((!person1.equals(person2))) {
            throw new AuthorizationException("You are not authorized to perform actions with the desired user");
        }
    }

    public boolean isAuthorized(Person person1, Person person2) {
        return person1.equals(person2);
    }
}
