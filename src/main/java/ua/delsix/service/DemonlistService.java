package ua.delsix.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.entity.User;
import ua.delsix.jpa.repository.DemonlistRepository;
import ua.delsix.util.UserUtil;

import java.util.Optional;

@Service
@Log4j2
public class DemonlistService {
    private final AuthorizationService authorizationService;
    private final DemonlistRepository demonlistRepository;
    private final UserUtil userUtil;

    public DemonlistService(AuthorizationService authorizationService,
                            DemonlistRepository demonlistRepository,
                            UserUtil userUtil) {
        this.authorizationService = authorizationService;
        this.demonlistRepository = demonlistRepository;
        this.userUtil = userUtil;
    }

    public Demonlist getDemonlistById(long id) throws EntityNotFoundException {
        Optional<Demonlist> demonlist = demonlistRepository.findById(id);

        if (demonlist.isEmpty()) {
            throw new EntityNotFoundException("Demonlist with id " + id + " not found");
        }

        return demonlist.get();
    }

    public void createDemonlist(Demonlist demonlist, UserDetails userDetails) {
        User user = userUtil.getUserFromUserDetails(userDetails);

        demonlist.setUser(user);
        demonlistRepository.save(demonlist);
        log.info("New demonlist {} of user {} has been created", demonlist.getId(), user.getUsername());
    }

    public void deleteDemonlist(long id, UserDetails userDetails) throws
            AuthorizationException,
            EntityNotFoundException {
        User user = userUtil.getUserFromUserDetails(userDetails);
        Demonlist demonlist = getDemonlistById(id);
        authorizationService.verifyOwnershipOfTheDemonlist(demonlist, user);

        if (!demonlistRepository.existsById(id)) {
            throw new EntityNotFoundException("Demonlist with id " + id + " not found");
        }

        demonlistRepository.deleteById(id);
        log.info("Demonlist {} of user {} has been deleted", id, user.getUsername());
    }
}
