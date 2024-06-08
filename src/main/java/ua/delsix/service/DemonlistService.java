package ua.delsix.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ua.delsix.dto.DemonlistDto;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.exception.MoreDemonlistsThanAllowed;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.entity.User;
import ua.delsix.jpa.repository.DemonlistRepository;
import ua.delsix.mapper.DemonlistMapper;
import ua.delsix.util.UserUtil;

import java.util.List;

@Service
@Log4j2
public class DemonlistService {
    private final AuthorizationService authorizationService;
    private final DemonlistRepository demonlistRepository;
    private final DemonlistMapper demonlistMapper;
    private final UserUtil userUtil;

    public DemonlistService(AuthorizationService authorizationService,
                            DemonlistRepository demonlistRepository,
                            DemonlistMapper demonlistMapper,
                            UserUtil userUtil) {
        this.authorizationService = authorizationService;
        this.demonlistRepository = demonlistRepository;
        this.demonlistMapper = demonlistMapper;
        this.userUtil = userUtil;
    }

    public Demonlist getDemonlistById(long id) throws EntityNotFoundException {
        return demonlistRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Demonlist with id " + id + " not found"));
    }

    public List<Demonlist> getDemonlistsByUserId(long userId, UserDetails userDetails) throws EntityNotFoundException {
        User user1 = userUtil.getUserById(userId);
        User user2 = userUtil.getUserFromUserDetails(userDetails);

        if (authorizationService.isAuthorized(user1, user2)) {
            return demonlistRepository.findAllByUser(user1);
        } else {
            return demonlistRepository.findAllByUserAndIsPublicTrue(user1);
        }
    }

    public void createDemonlist(Demonlist demonlist, UserDetails userDetails) throws MoreDemonlistsThanAllowed {
        User user = userUtil.getUserFromUserDetails(userDetails);

        demonlist.setUser(user);

        if (demonlistRepository.countByUser(user) >= 25) {
            throw new MoreDemonlistsThanAllowed();
        }

        demonlistRepository.save(demonlist);
        log.info("New demonlist {} of user {} has been created", demonlist.getId(), user.getUsername());
    }

    public void deleteDemonlist(long id, UserDetails userDetails) throws
            AuthorizationException,
            EntityNotFoundException {
        User user = userUtil.getUserFromUserDetails(userDetails);
        Demonlist demonlist = getDemonlistById(id);
        authorizationService.verifyOwnershipOfTheDemonlist(demonlist, user);

        demonlistRepository.deleteById(id);
        log.info("Demonlist {} of user {} has been deleted", id, user.getUsername());
    }

    public void updateDemonlist(long id, DemonlistDto dto, UserDetails userDetails) throws
            EntityNotFoundException,
            AuthorizationException {
        User user = userUtil.getUserFromUserDetails(userDetails);
        Demonlist demonlist = getDemonlistById(id);
        authorizationService.verifyOwnershipOfTheDemonlist(demonlist, user);
        demonlistMapper.updateDemonlistFromDto(dto, demonlist);

        demonlistRepository.save(demonlist);
        log.info("Demonlist {} of user {} has been updated", demonlist.getId(), user.getUsername());
    }
}
