package ua.delsix.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.delsix.dto.DemonDto;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.jpa.entity.Demon;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.entity.User;
import ua.delsix.jpa.repository.DemonRepository;
import ua.delsix.jpa.repository.DemonlistRepository;
import ua.delsix.mapper.DemonMapper;

@Service
@Log4j2
public class DemonService {
    private final DemonRepository demonRepository;
    private final DemonlistRepository demonlistRepository;
    private final DemonMapper demonMapper;
    private final AuthorizationService authorizationService;
    private final UserService userService;

    public DemonService(AuthorizationService authorizationService,
                        DemonRepository demonRepository,
                        DemonlistRepository demonlistRepository, DemonMapper demonMapper,
                        UserService userService) {
        this.authorizationService = authorizationService;
        this.demonRepository = demonRepository;
        this.demonMapper = demonMapper;
        this.userService = userService;
        this.demonlistRepository = demonlistRepository;
    }

    @Transactional
    public void createDemon(Demon demon, UserDetails userDetails) throws AuthorizationException {
        User user = userService.getUserFromUserDetails(userDetails);
        authorizationService.verifyOwnershipOfTheDemonlist(demon.getDemonlist(), user);

        int nextIndex = nextIndex(demon);

        if (demon.getPlacement() == null) {
            demon.setPlacement(nextIndex);
        }

        if (demonRepository.existsByPlacementAndDemonlistId(demon.getPlacement(), demon.getDemonlist().getId())) {
            log.info("Indexes {} to {} will be incremented", demon.getPlacement(), (nextIndex - 1));
            demonRepository.incrementPlacements(demon.getPlacement(), demon.getDemonlist().getId());
        } else if (demon.getPlacement() > nextIndex) {
            demon.setPlacement(nextIndex);
        }

        demonRepository.save(demon);
        log.info("New demon #{} {} of user {} has been created", demon.getId(), demon.getName(), user.getUsername());
    }

    @Transactional
    public void updateDemonPosition(long id, int newPosition, UserDetails userDetails) throws
            AuthorizationException,
            EntityNotFoundException {
        User user = userService.getUserFromUserDetails(userDetails);
        Demon demon = getDemonById(id);
        authorizationService.verifyOwnershipOfTheDemonlist(demon.getDemonlist(), user);
        int oldPosition = demon.getPlacement();

        System.out.println(oldPosition);
        System.out.println(newPosition);

        if (oldPosition > newPosition) {
            System.out.println("increment");
            demonRepository.incrementPlacementsBetween(newPosition, oldPosition, demon.getDemonlist().getId()); // old is smaller than new
        } else if (oldPosition < newPosition) {
            System.out.println("decrement");
            demonRepository.decrementPlacementsBetween(oldPosition, newPosition, demon.getDemonlist().getId()); // new is smaller than old
        }

        demon.setPlacement(newPosition);
        demonRepository.save(demon);
    }

    private int nextIndex(Demon demon) {
        return demonRepository.countByDemonlistId(demon.getDemonlist().getId()) + 1;
    }

    public void deleteDemon(long demonlistId, long demonId, UserDetails userDetails) throws AuthorizationException {
        User user = userService.getUserFromUserDetails(userDetails);
        Demonlist demonlist = demonlistRepository.getReferenceById(demonlistId);
        authorizationService.verifyOwnershipOfTheDemonlist(demonlist, user);

        if (!demonRepository.existsById(demonId)) {
            throw new EntityNotFoundException("Demon with id " + demonId + " not found");
        }

        demonRepository.deleteById(demonId);
        log.info("Demon #{} of user {} has been deleted", demonId, user.getUsername());
    }

    public void updateDemon(long id, DemonDto dto, UserDetails userDetails) throws
            EntityNotFoundException,
            AuthorizationException {
        User user = userService.getUserFromUserDetails(userDetails);
        Demon demon = getDemonById(id);
        authorizationService.verifyOwnershipOfTheDemonlist(demon.getDemonlist(), user);
        demonMapper.updateDemonFromDto(dto, demon);

        demonRepository.save(demon);
        log.info("Demon #{} {} of user {} has been updated", demon.getId(), demon.getName(), user.getUsername());
    }

    public Demon getDemonById(long id) {
        return demonRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Demon with id " + id + " not found"));
    }
}
