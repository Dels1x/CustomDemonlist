package ua.delsix.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.delsix.jpa.entity.Demon;

@Repository
public interface DemonRepository extends JpaRepository<Demon, Long> {
    int countByDemonlistId(long demonlistId);
    @Modifying
    @Transactional
    @Query("UPDATE Demon d SET d.placement = d.placement + 1 WHERE d.placement >= :placement AND d.demonlist.id = :demonlistId")
    void incrementTargetIndex(int placement, long demonlistId);
    boolean existsByPlacementAndDemonlistId(int placement, long demonlistId);
    void deleteByDemonlistUserId(long userId);
}