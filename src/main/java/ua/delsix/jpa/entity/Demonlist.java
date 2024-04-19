package ua.delsix.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "demonlist")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Demonlist {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "demonlist_id_gen")
    @SequenceGenerator(name = "demonlist_id_gen", sequenceName = "demonlist_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 80, columnDefinition = "BOOLEAN DEFAULT 'Demonlist'")
    private String name;

    @Column(name = "is_public", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isPublic = false;

    @Column(name = "is_multi", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isMulti = false;

    @OneToMany(mappedBy = "demonlist")
    private Set<Demon> demons = new LinkedHashSet<>();
}
