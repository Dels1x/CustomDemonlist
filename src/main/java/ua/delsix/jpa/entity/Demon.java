package ua.delsix.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ua.delsix.util.Views;

import java.time.LocalDate;

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

    @Column(name = "enjoyment_rating")
    @JsonView(Views.Public.class)
    private Integer enjoymentRating;

    @Column(name = "difficulty", length = 32)
    @JsonView(Views.Public.class)
    private String difficulty;

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
    private String gddlTier;

    @Column(name = "aredl_placement")
    @JsonView(Views.Public.class)
    private Integer aredlPlacement;

    @Column(name = "placement", nullable = false)
    @JsonView(Views.Public.class)
    private Integer placement;

    @Column(name = "initial_placement")
    @JsonView(Views.Public.class)
    private Integer initialPlacement;
}
