package ua.delsix.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.entity.Person;

import java.util.List;

@Repository
public interface DemonlistRepository extends JpaRepository<Demonlist, Long> {
    @Modifying
    @Transactional
    void deleteByPersonId(long personId);

    int countByPerson(Person person);

    int countByPersonId(long id);

    @Query("SELECT COUNT(d) FROM Demonlist d WHERE d.person.id = :personId AND d.isPublic = true")
    int countByPersonIdAndIsPublicTrue(Long personId);

    @Query("SELECT d From Demonlist d WHERE d.person = :person ORDER BY d.id ASC")
    List<Demonlist> findAllByPerson(Person person);

    @Query("SELECT d From Demonlist d WHERE d.person = :person AND d.isPublic = true ORDER BY d.id ASC")
    List<Demonlist> findAllByPersonAndIsPublicTrue(Person person);

    @Modifying
    @Transactional
    @Query("UPDATE Demonlist d SET d.name = :newName WHERE d.id = :id")
    void updateNameById(long id, String newName);
}
