package ua.delsix.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ua.delsix.util.Views;

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
    @JsonView(Views.Public.class)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonView(Views.Public.class)
    private User user;

    @Column(name = "name", nullable = false, length = 80, columnDefinition = "BOOLEAN DEFAULT 'Demonlist'")
    @JsonView(Views.Public.class)
    private String name;

    @Column(name = "is_public", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @JsonView(Views.Public.class)
    private Boolean isPublic = false;

    @Column(name = "is_multi", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @JsonView(Views.Public.class)
    private Boolean isMulti = false;

    @OneToMany(mappedBy = "demonlist")
    @JsonView(Views.Public.class)
    private Set<Demon> demons = new LinkedHashSet<>();
}
