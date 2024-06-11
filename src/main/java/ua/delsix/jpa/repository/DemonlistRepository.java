package ua.delsix.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.entity.User;

import java.util.List;

@Repository
public interface DemonlistRepository extends JpaRepository<Demonlist, Long> {
    @Modifying
    @Transactional
    void deleteByUserId(long userId);
    int countByUser(User user);

    List<Demonlist> findAllByUser(User user);
    List<Demonlist> findAllByUserAndIsPublicTrue(User user);
}
