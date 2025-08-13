package ua.delsix.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.delsix.dto.DemonlistDto;
import ua.delsix.dto.UserStatsDto;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.exception.DemonlistDoesntExistException;
import ua.delsix.exception.MoreDemonlistsThanAllowedException;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.entity.Person;
import ua.delsix.jpa.repository.DemonlistLikesRepository;
import ua.delsix.jpa.repository.DemonlistRepository;
import ua.delsix.mapper.DemonlistMapper;
import ua.delsix.util.DemonlistUtil;

import java.util.List;

@Service
@Log4j2
public class DemonlistService {
    private final AuthService authService;
    private final DemonlistRepository demonlistRepository;
    private final DemonlistLikesRepository demonlistLikesRepository;
    private final DemonlistMapper demonlistMapper;
    private final PersonService personService;
    private final DemonlistUtil demonlistUtil;
    private static final int DEMONLISTS_AMOUNT_LIMIT = 100;
    private final DemonService demonService;

    public DemonlistService(AuthService authService,
                            DemonlistRepository demonlistRepository,
                            DemonlistLikesRepository demonlistLikesRepository,
                            DemonlistMapper demonlistMapper,
                            PersonService personService,
                            DemonlistUtil demonlistUtil,
                            DemonService demonService) {
        this.authService = authService;
        this.demonlistRepository = demonlistRepository;
        this.demonlistLikesRepository = demonlistLikesRepository;
        this.demonlistMapper = demonlistMapper;
        this.personService = personService;
        this.demonlistUtil = demonlistUtil;
        this.demonService = demonService;
    }

    public Demonlist getDemonlistByIdAuth(long id, UserDetails userDetails)
            throws AuthorizationException,
            DemonlistDoesntExistException {
        Demonlist demonlist = demonlistUtil.getDemonlistThrowIfDoesntExist(id);
        Person person1 = demonlist.getPerson();
        Person person2 = userDetails == null ? null : personService.getUserFromUserDetails(userDetails);

        if (authService.isAuthorized(person1, person2) || demonlist.getIsPublic()) {
            return demonlist;
        }

        throw new AuthorizationException("User isn't authorized not the demonlist is public");
    }

    public List<Demonlist> getDemonlistsByUserId(long userId, UserDetails userDetails)
            throws EntityNotFoundException {
        Person person1 = personService.getUserById(userId);
        Person person2 = personService.getUserFromUserDetails(userDetails);

        if (authService.isAuthorized(person1, person2)) {
            return demonlistRepository.findAllByPerson(person1);
        } else {
            return demonlistRepository.findAllByPersonAndIsPublicTrue(person1);
        }
    }

    public void createDemonlist(Demonlist demonlist, UserDetails userDetails) throws MoreDemonlistsThanAllowedException {
        Person person = personService.getUserFromUserDetails(userDetails);
        demonlist.setPerson(person);

        if (demonlistRepository.countByPerson(person) >= DEMONLISTS_AMOUNT_LIMIT) {
            log.info("User {} has more demonlists than {}",
                    person.getUsername(),
                    DEMONLISTS_AMOUNT_LIMIT);
            throw new MoreDemonlistsThanAllowedException();
        }

        demonlistRepository.save(demonlist);
        log.info("New demonlist {} of user {} has been created", demonlist.getId(), person.getUsername());
    }

    @Transactional
    public void deleteDemonlist(long id, UserDetails userDetails) throws
            AuthorizationException,
            EntityNotFoundException,
            DemonlistDoesntExistException {
        Person person = personService.getUserFromUserDetails(userDetails);
        Demonlist demonlist = demonlistUtil.getDemonlistThrowIfDoesntExist(id);
        authService.verifyOwnershipOfTheDemonlist(demonlist, person);

        demonService.deleteDemonsBy(demonlist);
        demonlistRepository.deleteById(id);
        log.info("Demonlist {} of user {} has been deleted", id, person.getUsername());
    }

    @Transactional
    public void updateDemonlist(long id, DemonlistDto dto, UserDetails userDetails) throws
            EntityNotFoundException,
            AuthorizationException,
            DemonlistDoesntExistException {
        Person person = personService.getUserFromUserDetails(userDetails);
        Demonlist demonlist = demonlistUtil.getDemonlistThrowIfDoesntExist(id);
        authService.verifyOwnershipOfTheDemonlist(demonlist, person);
        demonlistMapper.updateDemonlistFromDto(dto, demonlist);

        demonlistRepository.save(demonlist);
        log.info("Demonlist {} of user {} has been updated", demonlist.getId(), person.getUsername());
    }

    @Transactional
    public void updateDemonlistName(long id, String newName, UserDetails userDetails) throws
            DemonlistDoesntExistException,
            AuthorizationException {
        Person person = personService.getUserFromUserDetails(userDetails);
        Demonlist demonlist = demonlistUtil.getDemonlistThrowIfDoesntExist(id);
        authService.verifyOwnershipOfTheDemonlist(demonlist, person);

        demonlistRepository.updateNameById(id, newName);
        log.info("Demonlist #{}'s name of user {} has been updated to {}",
                id,
                person.getUsername(),
                newName);
    }

    public int countByPersonId(long id, UserDetails userDetails) throws
            EntityNotFoundException,
            AuthorizationException {
        long id2 = personService.getUserFromUserDetails(userDetails).getId();

        if (id != id2) {
            throw new AuthorizationException(String.format("User %s has not authority on user %d",
                    userDetails.getUsername(),
                    id));
        }

        return demonlistRepository.countByPersonId(id);
    }

    public UserStatsDto getUserStats(long personId) {
        int demonsCount = demonService.countDemonsByPersonId(personId);
        int listsCount = demonlistRepository.countByPersonId(personId);
        int publicListsCount = demonlistRepository.countByPersonIdAndIsPublicTrue(personId);

        return new UserStatsDto(listsCount, demonsCount, publicListsCount, 0);
    }
}
