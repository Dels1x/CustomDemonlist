package ua.delsix.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import ua.delsix.enums.Difficulty;
import ua.delsix.util.Views;

import java.time.LocalDate;
import java.util.Objects;

@ToString
@Getter
@Setter
@Entity
@Table(name = "demon")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Demon {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "demon_id_gen")
    @SequenceGenerator(name = "demon_id_gen", sequenceName = "demon_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    @JsonView(Views.Public.class)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "demonlist_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Demonlist demonlist;

    @Column(name = "name", nullable = false, length = 32)
    @JsonView(Views.Public.class)
    private String name;

    @Column(name = "author", length = 32)
    @JsonView(Views.Public.class)
    private String author;

    @Column(name = "attempts_count")
    @JsonView(Views.Public.class)
    private Integer attemptsCount;

    @Column(name = "worst_fail")
    @JsonView(Views.Public.class)
    private Integer worstFail;

    @Column(name = "enjoyment_rating")
    @JsonView(Views.Public.class)
    private Integer enjoymentRating;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", length = 32)
    @JsonView(Views.Public.class)
    private Difficulty difficulty;

    @Column(name = "date_of_completion")
    @JsonView(Views.Public.class)
    private LocalDate dateOfCompletion;

    @Column(name = "gddp_difficulty", length = 32)
    @JsonView(Views.Public.class)
    private String gddpDifficulty;

    @Column(name = "nlw_tier", length = 32)
    @JsonView(Views.Public.class)
    private String nlwTier;

    @Column(name = "gddl_tier", length = 32)
    @JsonView(Views.Public.class)
    private Integer gddlTier;

    @Column(name = "aredl_placement")
    @JsonView(Views.Public.class)
    private Integer aredlPlacement;

    @Column(name = "placement", nullable = false)
    @JsonView(Views.Public.class)
    private Integer placement;

    @Column(name = "initial_placement")
    @JsonView(Views.Public.class)
    private Integer initialPlacement;

    @Column(name = "in_game_id")
    @JsonView(Views.Public.class)
    private Integer inGameId;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Demon demon = (Demon) o;
        return getId() != null && Objects.equals(getId(), demon.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
