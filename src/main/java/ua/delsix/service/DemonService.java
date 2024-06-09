package ua.delsix.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.jpa.entity.Demon;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.entity.User;
import ua.delsix.jpa.repository.DemonRepository;
import ua.delsix.jpa.repository.DemonlistRepository;

@Service
@Log4j2
public class DemonService {
    private final DemonlistRepository demonlistRepository;
    private final AuthorizationService authorizationService;
    private final DemonRepository demonRepository;
    private final UserService userService;

    public DemonService(AuthorizationService authorizationService,
                        DemonRepository demonRepository,
                        DemonlistRepository demonlistRepository,
                        UserService userService) {
        this.authorizationService = authorizationService;
        this.demonRepository = demonRepository;
        this.userService = userService;
        this.demonlistRepository = demonlistRepository;
    }

    @Transactional
    public void createDemon(Demon demon, UserDetails userDetails) throws AuthorizationException {
        User user = userService.getUserFromUserDetails(userDetails);
        Demonlist demonlist = demon.getDemonlist();
        authorizationService.verifyOwnershipOfTheDemonlist(demonlist, user);

        int nextIndex = nextIndex(demon);

        if (demon.getPlacement() == null) {
            demon.setPlacement(nextIndex);
        }

        if (demonRepository.existsByPlacementAndDemonlistId(demon.getPlacement(), demon.getDemonlist().getId())) {
            log.info("Indexes {} to {} will be incremented", demon.getPlacement(), (nextIndex - 1));
            demonRepository.incrementTargetIndex(demon.getPlacement(), demon.getDemonlist().getId());
        } else if (demon.getPlacement() > nextIndex) {
            demon.setPlacement(nextIndex);
        }

        if (demon.getName() == null) {
            demon.setName("Demon #" + nextIndex);
        }

        demonRepository.save(demon);
        log.info("New demon {} of user {} has been created", demon.getId(), user.getUsername());
    }

    public void deleteDemon(long demonlistId, long demonId, UserDetails userDetails) throws AuthorizationException {
        User user = userService.getUserFromUserDetails(userDetails);
        Demonlist demonlist = demonlistRepository.getReferenceById(demonlistId);
        authorizationService.verifyOwnershipOfTheDemonlist(demonlist, user);

        if (!demonRepository.existsById(demonId)) {
            throw new EntityNotFoundException("Demon with id " + demonId + " not found");
        }

        demonRepository.deleteById(demonId);
        log.info("Demon {} of user {} has been deleted", demonId, user.getUsername());
    }

    private int nextIndex(Demon demon) {
        return demonRepository.countByDemonlistId(demon.getDemonlist().getId()) + 1;
    }
}
