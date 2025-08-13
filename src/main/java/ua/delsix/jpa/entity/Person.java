package ua.delsix.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ua.delsix.util.Views;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@Table(name = "\"person\"")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_id_gen")
    @SequenceGenerator(name = "person_id_gen", sequenceName = "person_id_gen", allocationSize = 1)
    @Column(name = "id", nullable = false)
    @JsonView(Views.Public.class)
    private Long id;

    @Column(name = "username", nullable = false, length = 40)
    @JsonView(Views.Public.class)
    private String username;

    @OneToMany(mappedBy = "person")
    @ToString.Exclude
    @JsonIgnore
    private Set<Demonlist> demonlists = new LinkedHashSet<>();

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "created_at")
    @JsonView(Views.Public.class)
    private Instant createdAt;

    @Column(name = "pfp_url")
    @JsonView(Views.Public.class)
    private String pfpUrl;

    @Column(name = "discord_id")
    private String discordId;

    @Column(name = "google_id")
    private String googleId;


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return getId() != null && Objects.equals(getId(), person.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

}
