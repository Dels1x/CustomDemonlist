package ua.delsix.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.delsix.enums.Difficulty;
import ua.delsix.jpa.entity.Demon;
import ua.delsix.jpa.entity.Demonlist;

@Repository
public interface DemonRepository extends JpaRepository<Demon, Long> {
    int countByDemonlistId(long demonlistId);

    @Modifying
    @Transactional
    @Query("UPDATE Demon d SET d.placement = d.placement + 1 WHERE d.placement >= :placement " +
            "AND d.demonlist.id = :demonlistId")
    void incrementPlacements(int placement, long demonlistId);

    @Modifying
    @Transactional
    @Query("UPDATE Demon d SET d.placement = d.placement + 1 WHERE d.placement >= :low " +
            "AND d.placement < :high AND d.demonlist.id = :demonlistId")
    void incrementPlacementsBetween(int low, int high, long demonlistId);

    @Modifying
    @Transactional
    @Query("UPDATE Demon d SET d.placement = d.placement - 1 WHERE d.placement >= :placement " +
            "AND d.demonlist.id = :demonlistId")
    void decrementPlacementsBelow(int placement, long demonlistId);

    @Modifying
    @Transactional
    @Query("UPDATE Demon d SET d.placement = d.placement - 1 WHERE d.placement > :low " +
            "AND d.placement <= :high AND d.demonlist.id = :demonlistId")
    void decrementPlacementsBetween(int low, int high, long demonlistId);

    boolean existsByPlacementAndDemonlistId(int placement, long demonlistId);

    @Modifying
    @Transactional
    void deleteByDemonlistPersonId(long personId);

    @Modifying
    @Transactional
    @Query("UPDATE Demon d SET d.placement = :placement WHERE d.id = :id")
    void setPlacementById(int placement, long id);

    @Modifying
    @Transactional
    @Query("UPDATE Demon d SET d.name = :name WHERE d.id = :id")
    void updateNameById(long id, String name);

    @Modifying
    @Transactional
    @Query("UPDATE Demon d SET d.author = :author WHERE d.id = :id")
    void updateAuthorById(long id, String author);

    @Modifying
    @Transactional
    @Query("UPDATE Demon d SET d.attemptsCount = :attemptsCount WHERE d.id = :id")
    void updateAttemptsCountById(long id, int attemptsCount);

    @Modifying
    @Transactional
    @Query("UPDATE Demon d SET d.enjoymentRating = :enjoymentRating WHERE d.id = :id")
    void updateEnjoymentRatingById(long id, int enjoymentRating);

    @Modifying
    @Transactional
    @Query("UPDATE Demon d SET d.difficulty = :difficulty WHERE d.id = :id")
    void updateDifficultyById(long id, Difficulty difficulty);

    @Modifying
    @Transactional
    @Query("DELETE FROM Demon d WHERE d.demonlist = :demonlist")
    void deleteByDemonlist(Demonlist demonlist);
}
