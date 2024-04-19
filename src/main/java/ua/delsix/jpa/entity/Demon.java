package ua.delsix.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "demonlist_id", nullable = false)
    @ToString.Exclude
    private Demonlist demonlist;

    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @Column(name = "author", length = 32)
    private String author;

    @Column(name = "attempts_count")
    private Integer attemptsCount;

    @Column(name = "enjoyment_rating")
    private Integer enjoymentRating;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
}
