package ua.delsix.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.entity.User;
import ua.delsix.jpa.repository.DemonlistRepository;
import ua.delsix.utils.UserUtils;

@Service
@Log4j2
public class DemonlistService {
    private final AuthorizationService authorizationService;
    private final DemonlistRepository demonlistRepository;
    private final UserUtils userUtils;

    public DemonlistService(AuthorizationService authorizationService,
                            DemonlistRepository demonlistRepository,
                            UserUtils userUtils) {
        this.authorizationService = authorizationService;
        this.demonlistRepository = demonlistRepository;
        this.userUtils = userUtils;
    }

    public Demonlist getDemonlistById(long id) {
        return demonlistRepository.getReferenceById(id);
    }

    public void createDemonlist(Demonlist demonlist, UserDetails userDetails) {
        User user = userUtils.getUserFromUserDetails(userDetails);

        demonlist.setUser(user);
        demonlistRepository.save(demonlist);
        log.info("New demonlist {} of user {} has been created", demonlist.getId(), user.getUsername());
    }

    public void deleteDemonlist(long demonlistId, UserDetails userDetails) throws AuthorizationException {
        User user = userUtils.getUserFromUserDetails(userDetails);
        Demonlist demonlist = getDemonlistById(demonlistId);
        authorizationService.verifyOwnershipOfTheDemonlist(demonlist, user);

        demonlistRepository.deleteById(demonlistId);
        log.info("Demonlist {} of user {} has been deleted", demonlistId, user.getUsername());
    }
}
