package ua.delsix.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.entity.User;
import ua.delsix.jpa.repository.DemonlistRepository;
import ua.delsix.jpa.repository.UserRepository;

@Service
@Log4j2
public class DemonlistService {
    private final DemonlistRepository demonlistRepository;
    private final UserRepository userRepository;

    public DemonlistService(DemonlistRepository demonlistRepository,
                            UserRepository userRepository) {
        this.demonlistRepository = demonlistRepository;
        this.userRepository = userRepository;
    }

    public Demonlist getDemonlistById(long id) {
        return demonlistRepository.getReferenceById(id);
    }

    public void createDemonlist(Demonlist demonlist, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername());

        demonlist.setUser(user);
        demonlistRepository.save(demonlist);
        log.info("New demonlist {} of user {} created", demonlist.getName(), user.getUsername());
    }
}
