package ua.delsix.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.delsix.dto.DemonDto;
import ua.delsix.enums.Difficulty;
import ua.delsix.exception.*;
import ua.delsix.jpa.entity.Demon;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.entity.Person;
import ua.delsix.jpa.repository.DemonRepository;
import ua.delsix.mapper.DemonMapper;
import ua.delsix.util.DemonUtil;

import java.time.LocalDate;

@Service
@Log4j2
public class DemonService {
    private final DemonRepository demonRepository;
    private final DemonMapper demonMapper;
    private final AuthService authService;
    private final PersonService personService;
    private final GddlService gddlService;
    private final AredlService aredlService;
    private final DemonUtil demonUtil;

    public DemonService(AuthService authService,
                        DemonRepository demonRepository,
                        DemonMapper demonMapper,
                        PersonService personService,
                        GddlService gddlService,
                        AredlService aredlService,
                        DemonUtil demonUtil) {
        this.authService = authService;
        this.demonRepository = demonRepository;
        this.demonMapper = demonMapper;
        this.personService = personService;
        this.gddlService = gddlService;
        this.aredlService = aredlService;
        this.demonUtil = demonUtil;
    }

    @Transactional
    public Demon createDemon(Demon demon, UserDetails userDetails) throws AuthorizationException {
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

        return demon;
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

        int max = countByDemonlist(demon.getDemonlist().getId());

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

    private void decrementPlacementsBelow(int deletedPos, long demonlistId) {
        demonRepository.decrementPlacementsBelow(deletedPos, demonlistId);
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
        return countByDemonlist(demon.getDemonlist().getId()) + 1;
    }

    public int countByDemonlist(long id) {
        return demonRepository.countByDemonlistId(id);
    }

    @Transactional
    public void deleteDemon(long id, UserDetails userDetails) throws AuthorizationException,
            DemonlistDoesntExistException {
        Person person = personService.getUserFromUserDetails(userDetails);
        Demon demon = getDemonById(id);
        authService.verifyOwnershipOfTheDemonlist(demon.getDemonlist(), person);
        log.info("Demon: {}", demon);

        demonRepository.deleteById(id);
        decrementPlacementsBelow(demon.getPlacement(), demon.getDemonlist().getId());
        log.info("Demon #{} of user {} has been deleted", id, person.getUsername());
    }

    public Demon getDemonById(long id) {
        return demonRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Demon with id " + id + " not found"));
    }

    @Transactional
    public Demon updateDemonName(long id, String newName, UserDetails userDetails) throws
            InvalidNameException,
            DemonDoesntExistException,
            AuthorizationException {
        if (newName == null || newName.isEmpty()) {
            throw new InvalidNameException("Name must not be null");
        }

        if (newName.length() > 32) {
            throw new InvalidNameException("Name must not exceed 32 characters");
        }

        Demon demon = demonUtil.getDemonThrowIfDoesntExist(id);
        Person person = personService.getUserFromUserDetails(userDetails);
        authService.verifyOwnershipOfTheDemonlist(demon.getDemonlist(), person);

        demon.setName(newName);
        updateGddl(demon);
        addAredlPositionToDemon(demon);

        return demon;
    }

    @Transactional
    public Demon updateDemonAuthor(long id, String newAuthor, UserDetails userDetails) throws
            InvalidAuthorException,
            DemonDoesntExistException,
            AuthorizationException {
        if (newAuthor == null || newAuthor.isEmpty()) {
            throw new InvalidAuthorException("Author must not be null");
        }

        if (newAuthor.length() > 32) {
            throw new InvalidAuthorException("Author must not exceed 32 characters");
        }

        Demon demon = demonUtil.getDemonThrowIfDoesntExist(id);
        Person person = personService.getUserFromUserDetails(userDetails);
        authService.verifyOwnershipOfTheDemonlist(demon.getDemonlist(), person);

        demon.setAuthor(newAuthor);
        updateGddl(demon);
        addAredlPositionToDemon(demon);

        return demon;
    }

    private void updateGddl(Demon demon) {
        JSONObject gddlData = gddlService.searchLevel(demon.getName(), demon.getAuthor());

        if (gddlData != null && gddlData.has("Rating") && !gddlData.isNull("Rating")) {
            double rawRating = gddlData.getDouble("Rating");
            int roundedRating = (int) Math.round(rawRating);

            demon.setGddlTier(roundedRating);
            log.info("Rounded Rating: {}", roundedRating);
        }

        if (gddlData != null && gddlData.has("Meta") && !gddlData.isNull("Meta")) {
            JSONObject meta = gddlData.getJSONObject("Meta");

            if (meta.has("ID") && !meta.isNull("ID")) {
                int id = meta.getInt("ID");

                demon.setInGameId(id);
                log.info("In Game ID: {}", id);
            }

            if (meta.has("Name") && !meta.isNull("Name")) {
                if (demon.getName().startsWith("Demon #")) {
                    String name = meta.getString("Name");

                    demon.setName(name);
                    log.info("Name: {}", name);
                }
            }

            if (meta.has("Creator") && !meta.isNull("Creator")) {
                if (demon.getAuthor().equals("Author")) {
                    String creator = meta.getString("Creator");

                    demon.setAuthor(creator);
                    log.info("Author: {}", creator);
                }
            }

            if (meta.has("Difficulty") && !meta.isNull("Difficulty")) {
                if (demon.getDifficulty() == null) {
                    String difficulty = meta.getString("Difficulty");

                    switch (difficulty) {
                        case "Easy" -> demon.setDifficulty(Difficulty.EASY_DEMON);
                        case "Medium" -> demon.setDifficulty(Difficulty.MEDIUM_DEMON);
                        case "Hard" -> demon.setDifficulty(Difficulty.HARD_DEMON);
                        case "Insane" -> demon.setDifficulty(Difficulty.INSANE_DEMON);
                        case "Extreme" -> demon.setDifficulty(Difficulty.EXTREME_DEMON);
                    }

                    log.info("Difficulty: {}", difficulty);
                }
            }
        }
    }

    private void addAredlPositionToDemon(Demon demon) {
        int position = aredlService.getPositionForLevel(demon.getInGameId());
        demon.setAredlPlacement(position);
    }

    @Transactional
    public void updateDemonAttemptsCount(long id, int attemptsCount, UserDetails userDetails) throws
            DemonDoesntExistException,
            AuthorizationException {
        Demon demon = demonUtil.getDemonThrowIfDoesntExist(id);
        Person person = personService.getUserFromUserDetails(userDetails);
        authService.verifyOwnershipOfTheDemonlist(demon.getDemonlist(), person);

        demonRepository.updateAttemptsCountById(id, attemptsCount);
    }

    @Transactional
    public void updateDemonEnjoymentRating(long id, int enjoymentRating, UserDetails userDetails) throws
            DemonDoesntExistException,
            AuthorizationException {
        Demon demon = demonUtil.getDemonThrowIfDoesntExist(id);
        Person person = personService.getUserFromUserDetails(userDetails);
        authService.verifyOwnershipOfTheDemonlist(demon.getDemonlist(), person);

        demonRepository.updateEnjoymentRatingById(id, enjoymentRating);
    }

    @Transactional
    public void updateDemonDifficulty(long id, Difficulty difficulty, UserDetails userDetails) throws
            DemonDoesntExistException,
            AuthorizationException {
        Demon demon = demonUtil.getDemonThrowIfDoesntExist(id);
        Person person = personService.getUserFromUserDetails(userDetails);
        authService.verifyOwnershipOfTheDemonlist(demon.getDemonlist(), person);

        demonRepository.updateDifficultyById(id, difficulty);
    }

    @Transactional
    public void updateDemonDateOfCompletion(long id, LocalDate date, UserDetails userDetails) throws
            DemonDoesntExistException,
            AuthorizationException {
        Demon demon = demonUtil.getDemonThrowIfDoesntExist(id);
        Person person = personService.getUserFromUserDetails(userDetails);
        authService.verifyOwnershipOfTheDemonlist(demon.getDemonlist(), person);

        demonRepository.updateDateOfCompletionById(id, date);
    }

    @Transactional
    public void updateWorstFail(long id, int worstFail, UserDetails userDetails) throws
            DemonDoesntExistException,
            AuthorizationException {
        Demon demon = demonUtil.getDemonThrowIfDoesntExist(id);
        Person person = personService.getUserFromUserDetails(userDetails);
        authService.verifyOwnershipOfTheDemonlist(demon.getDemonlist(), person);

        demonRepository.updateWorstFailById(id, worstFail);
    }

    public void deleteDemonsBy(Demonlist demonlist) {
        demonRepository.deleteByDemonlist(demonlist);
    }
}
