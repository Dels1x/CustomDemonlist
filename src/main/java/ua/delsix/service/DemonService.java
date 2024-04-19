package ua.delsix.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.jpa.entity.Demon;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.entity.User;
import ua.delsix.jpa.repository.DemonRepository;
import ua.delsix.jpa.repository.UserRepository;

@Service
public class DemonService {
    private final DemonRepository demonRepository;
    private final UserRepository userRepository;

    public DemonService(DemonRepository demonRepository, UserRepository userRepository) {
        this.demonRepository = demonRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createDemon(Demon demon, UserDetails userDetails) throws AuthorizationException {
        User user = userRepository.findByUsername(userDetails.getUsername());
        Demonlist demonlist = demon.getDemonlist();

        // verifying that the user is in fact the owner of the specified demonlist
        if (demonlist == null || demonlist.getUser().equals(user)) {
            throw new AuthorizationException("You are not authorized to create a demon in this demonlist");
        }

        int nextIndex = nextIndex(demon);

        if (demon.getOrderIndex() == null) {
            demon.setOrderIndex(nextIndex);
        }

        if (demonRepository.existsByOrderIndexAndDemonlistId(demon.getOrderIndex(), demon.getDemonlist().getId())) {
            demonRepository.incrementTargetIndex(demon.getOrderIndex(), demon.getDemonlist().getId());
        } else if (demon.getOrderIndex() > nextIndex) {
            demon.setOrderIndex(nextIndex);
        }

        if (demon.getName() == null) {
            demon.setName("Demon #" + nextIndex);
        }

        demonRepository.save(demon);
    }

    private int nextIndex(Demon demon) {
        return demonRepository.countByDemonlistId(demon.getDemonlist().getId()) + 1;
    }
}
