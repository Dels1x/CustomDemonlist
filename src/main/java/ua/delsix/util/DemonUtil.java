package ua.delsix.util;

import org.springframework.stereotype.Component;
import ua.delsix.exception.DemonDoesntExistException;
import ua.delsix.jpa.entity.Demon;
import ua.delsix.jpa.repository.DemonRepository;

@Component
public class DemonUtil {
    private final DemonRepository demonRepository;

    public DemonUtil(DemonRepository demonRepository) {
        this.demonRepository = demonRepository;
    }

    public Demon getDemonThrowIfDoesntExist(long id) throws DemonDoesntExistException {
        return demonRepository.findById(id).orElseThrow(() ->
                new DemonDoesntExistException(String.format("Demon %d doesn't exist", id))
        );
    }
}
