package ua.delsix.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ua.delsix.dto.DemonlistDto;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.exception.MoreDemonlistsThanAllowed;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.entity.Person;
import ua.delsix.jpa.repository.DemonlistRepository;
import ua.delsix.mapper.DemonlistMapper;

import java.util.List;

@Service
@Log4j2
public class DemonlistService {
    private final AuthorizationService authorizationService;
    private final DemonlistRepository demonlistRepository;
    private final DemonlistMapper demonlistMapper;
    private final PersonService personService;

    public DemonlistService(AuthorizationService authorizationService,
                            DemonlistRepository demonlistRepository,
                            DemonlistMapper demonlistMapper,
                            PersonService personService) {
        this.authorizationService = authorizationService;
        this.demonlistRepository = demonlistRepository;
        this.demonlistMapper = demonlistMapper;
        this.personService = personService;
    }

    public Demonlist getDemonlistById(long id) throws EntityNotFoundException {
        return demonlistRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Demonlist with id " + id + " not found"));
    }

    public List<Demonlist> getDemonlistsByUserId(long userId, UserDetails userDetails) throws EntityNotFoundException {
        Person person1 = personService.getUserById(userId);
        Person person2 = personService.getUserFromUserDetails(userDetails);

        if (authorizationService.isAuthorized(person1, person2)) {
            return demonlistRepository.findAllByPerson(person1);
        } else {
            return demonlistRepository.findAllByPersonAndIsPublicTrue(person1);
        }
    }

    public void createDemonlist(Demonlist demonlist, UserDetails userDetails) throws MoreDemonlistsThanAllowed {
        Person person = personService.getUserFromUserDetails(userDetails);

        demonlist.setPerson(person);

        if (demonlistRepository.countByPerson(person) >= 25) {
            throw new MoreDemonlistsThanAllowed();
        }

        demonlistRepository.save(demonlist);
        log.info("New demonlist {} of user {} has been created", demonlist.getId(), person.getUsername());
    }

    public void deleteDemonlist(long id, UserDetails userDetails) throws
            AuthorizationException,
            EntityNotFoundException {
        Person person = personService.getUserFromUserDetails(userDetails);
        Demonlist demonlist = getDemonlistById(id);
        authorizationService.verifyOwnershipOfTheDemonlist(demonlist, person);

        demonlistRepository.deleteById(id);
        log.info("Demonlist {} of user {} has been deleted", id, person.getUsername());
    }

    public void updateDemonlist(long id, DemonlistDto dto, UserDetails userDetails) throws
            EntityNotFoundException,
            AuthorizationException {
        Person person = personService.getUserFromUserDetails(userDetails);
        Demonlist demonlist = getDemonlistById(id);
        authorizationService.verifyOwnershipOfTheDemonlist(demonlist, person);
        demonlistMapper.updateDemonlistFromDto(dto, demonlist);

        demonlistRepository.save(demonlist);
        log.info("Demonlist {} of user {} has been updated", demonlist.getId(), person.getUsername());
    }
}
