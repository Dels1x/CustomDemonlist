package ua.delsix.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.delsix.jpa.entity.Demonlist;

@Repository
public interface DemonlistRepository extends JpaRepository<Demonlist, Long> {
}