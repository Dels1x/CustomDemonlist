package ua.delsix.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.delsix.jpa.entity.Person;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findOptionalByUsername(String username);
    Optional<Person> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
