package ua.delsix.util;

import org.springframework.stereotype.Component;
import ua.delsix.exception.DemonlistDoesntExistException;
import ua.delsix.jpa.entity.Demon;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.repository.DemonlistRepository;

@Component
public class DemonlistUtil {
    private final DemonlistRepository demonlistRepository;

    public DemonlistUtil(DemonlistRepository demonlistRepository) {
        this.demonlistRepository = demonlistRepository;
    }

    public void LinkDemonlistToDemon(Demon demon, long id) throws DemonlistDoesntExistException {
        demon.setDemonlist(getDemonlistThrowIfDoesntExist(id));
    }

    public Demonlist getDemonlistThrowIfDoesntExist(long id) throws DemonlistDoesntExistException {
        return demonlistRepository.findById(id).orElseThrow(() ->
                new DemonlistDoesntExistException(String.format("Demonlist %d doesn't exist", id))
        );
    }
}
