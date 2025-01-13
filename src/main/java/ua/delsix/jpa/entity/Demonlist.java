package ua.delsix.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import ua.delsix.util.Views;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "demonlist")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString
public class Demonlist {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "demonlist_id_gen")
    @SequenceGenerator(name = "demonlist_id_gen", sequenceName = "demonlist_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    @JsonView({Views.Public.class, Views.Superficial.class})
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    @JsonView(Views.Public.class)
    private Person person;

    @Column(name = "name", nullable = false, length = 80, columnDefinition = "BOOLEAN DEFAULT 'Demonlist'")
    @JsonView({Views.Public.class, Views.Superficial.class})
    private String name;

    @Column(name = "is_public", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @JsonView({Views.Public.class, Views.Superficial.class})
    private Boolean isPublic = false;

    @Column(name = "is_multi", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @JsonView({Views.Public.class, Views.Superficial.class})
    private Boolean isMulti = false;

    @OneToMany(mappedBy = "demonlist")
    @JsonView(Views.Public.class)
    @ToString.Exclude
    private Set<Demon> demons = new LinkedHashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Demonlist demonlist = (Demonlist) o;
        return getId() != null && Objects.equals(getId(), demonlist.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
