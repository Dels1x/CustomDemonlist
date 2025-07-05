package ua.delsix.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.delsix.jpa.entity.DemonlistLikes;

@Repository
public interface DemonlistLikesRepository extends JpaRepository<DemonlistLikes, Long> {
}