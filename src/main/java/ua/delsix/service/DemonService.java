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
import ua.delsix.jpa.entity.Person;
import ua.delsix.jpa.repository.DemonRepository;
import ua.delsix.jpa.repository.DemonlistRepository;
import ua.delsix.mapper.DemonMapper;

@Service
@Log4j2
public class DemonService {
    private final DemonRepository demonRepository;
    private final DemonlistRepository demonlistRepository;
    private final DemonMapper demonMapper;
    private final AuthService authService;
    private final PersonService personService;

    public DemonService(AuthService authService,
                        DemonRepository demonRepository,
                        DemonlistRepository demonlistRepository, DemonMapper demonMapper,
                        PersonService personService) {
        this.authService = authService;
        this.demonRepository = demonRepository;
        this.demonMapper = demonMapper;
        this.personService = personService;
        this.demonlistRepository = demonlistRepository;
    }

    @Transactional
    public void createDemon(Demon demon, UserDetails userDetails) throws AuthorizationException {
        Person person = personService.getUserFromUserDetails(userDetails);
        authService.verifyOwnershipOfTheDemonlist(demon.getDemonlist(), person);

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
        log.info("New demon #{} {} of user {} has been created", demon.getId(), demon.getName(), person.getUsername());
    }


    @Transactional
    public void updateDemonPosition(long id, int newPosition, UserDetails userDetails) throws
            AuthorizationException,
            EntityNotFoundException {
        Person person = personService.getUserFromUserDetails(userDetails);
        Demon demon = getDemonById(id);
        authService.verifyOwnershipOfTheDemonlist(demon.getDemonlist(), person);
        int oldPosition = demon.getPlacement();

        editPlacements(demon, newPosition, oldPosition);
    }

    private void editPlacements(Demon demon, int newPos, int oldPos) {
        log.debug("old position: {}; new position: {}", oldPos, newPos);

        int max = countByDemonlist(demon.getDemonlist());

        if (newPos > max) {
            newPos = max;
        }

        if (newPos < 1) {
            newPos = 1;
        }

        if (oldPos > newPos) {
            demonRepository.incrementPlacementsBetween(newPos, oldPos, demon.getDemonlist().getId()); // old is smaller than new
        } else if (oldPos < newPos) {
            demonRepository.decrementPlacementsBetween(oldPos, newPos, demon.getDemonlist().getId()); // new is smaller than old
        } else {
            return;
        }

        demon.setPlacement(newPos);
        log.info("demon's placement {}", newPos);
        demonRepository.setPlacementById(newPos, demon.getId());
        demonRepository.flush();
    }

    @Transactional
    public void updateDemon(long id, DemonDto dto, UserDetails userDetails) throws
            EntityNotFoundException,
            AuthorizationException,
            IllegalArgumentException {
        Person person = personService.getUserFromUserDetails(userDetails);
        Demon demon = getDemonById(id);
        authService.verifyOwnershipOfTheDemonlist(demon.getDemonlist(), person);
        int oldPos = demon.getPlacement();
        demonMapper.updateDemonFromDto(dto, demon);
        log.info("dto {}", dto);

        if (demon.getPlacement() == null || demon.getName() == null) {
            throw new IllegalArgumentException("Placement and name fields cannot be null");
        }

        demonRepository.save(demon);

        int newPos = demon.getPlacement();
        if (oldPos != newPos) {
            log.info("oldPos: {}; newPos: {}", oldPos, newPos);
            editPlacements(demon, newPos, oldPos);
        }

        log.info("Demon #{} {} of user {} has been updated", demon.getId(), demon.getName(), person.getUsername());
    }

    private int nextIndex(Demon demon) {
        return countByDemonlist(demon.getDemonlist()) + 1;
    }

    private int countByDemonlist(Demonlist demonlist) {
        return demonRepository.countByDemonlistId(demonlist.getId());
    }

    public void deleteDemon(long demonlistId, long demonId, UserDetails userDetails) throws AuthorizationException {
        Person person = personService.getUserFromUserDetails(userDetails);
        Demonlist demonlist = demonlistRepository.getReferenceById(demonlistId);
        authService.verifyOwnershipOfTheDemonlist(demonlist, person);

        if (!demonRepository.existsById(demonId)) {
            throw new EntityNotFoundException("Demon with id " + demonId + " not found");
        }

        demonRepository.deleteById(demonId);
        log.info("Demon #{} of user {} has been deleted", demonId, person.getUsername());
    }

    public Demon getDemonById(long id) {
        return demonRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Demon with id " + id + " not found"));
    }
}
