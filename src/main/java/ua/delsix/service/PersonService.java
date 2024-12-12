package ua.delsix.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.delsix.dto.DiscordUserDto;
import ua.delsix.exception.UsernameAlreadyExists;
import ua.delsix.jpa.entity.Person;
import ua.delsix.jpa.repository.DemonRepository;
import ua.delsix.jpa.repository.DemonlistRepository;
import ua.delsix.jpa.repository.PersonRepository;
import ua.delsix.mapper.PersonMapper;
import ua.delsix.security.JwtUtil;

import java.time.Instant;

@Service
@Log4j2
public class PersonService {
    private final DemonlistRepository demonlistRepository;
    private final DemonRepository demonRepository;
    private final PersonRepository personRepository;
    private final JwtUtil jwtUtil;

    public PersonService(DemonlistRepository demonlistRepository,
                         DemonRepository demonRepository,
                         PersonRepository personRepository, JwtUtil jwtUtil) {
        this.demonlistRepository = demonlistRepository;
        this.demonRepository = demonRepository;
        this.personRepository = personRepository;
        this.jwtUtil = jwtUtil;
    }

    public Person getUserById(long id) throws EntityNotFoundException {
        return personRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    public Person getUserByUsername(String username) throws EntityNotFoundException {
        return personRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User with username " + username + " not found"));
    }

    public Person getUserFromUserDetails(UserDetails userDetails) throws EntityNotFoundException {
        return getUserByUsername(userDetails.getUsername());
    }

    public void createUser(Person person) throws UsernameAlreadyExists, EmailAlreadyExists {
        if (personRepository.existsByUsername(person.getUsername())) {
            String errorMessage = String.format("User %s already exists", person.getUsername());
            log.info(errorMessage);
            throw new UsernameAlreadyExists(errorMessage);
        }
        if (personRepository.existsByEmail(person.getEmail())) {
            String errorMessage = String.format("User %s with email %s that already exists", person.getUsername(), person.getEmail());
            log.info(errorMessage);
            throw new EmailAlreadyExists(errorMessage);
        }

        person.setCreatedAt(Instant.now());
        personRepository.save(person);
        log.info("New user {} created", person.getUsername());
    }

    @Transactional
    public Person createUserByDiscordUser(DiscordUserDto discordUserDTO) {
        return personRepository.findById(Long.valueOf(discordUserDTO.getId()))
                .map(existingUser -> {
                    existingUser.setUsername(discordUserDTO.getUsername());
                    existingUser.setEmail(discordUserDTO.getEmail());
                    existingUser.setPfpUrl(String.format("https://cdn.discordapp.com/avatars/%s/%s.png",
                            discordUserDTO.getId(), discordUserDTO.getAvatar()));
                    existingUser = personRepository.save(existingUser);

                    String accessToken = jwtUtil.generateAccessToken(existingUser);
                    String refreshToken = jwtUtil.generateRefreshToken(existingUser);

                    existingUser.setAccessToken(accessToken);
                    existingUser.setRefreshToken(refreshToken);

                    return personRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    Person newUser = PersonMapper.INSTANCE.toEntity(discordUserDTO);

                    newUser = personRepository.save(newUser);

                    String accessToken = jwtUtil.generateAccessToken(newUser);
                    String refreshToken = jwtUtil.generateRefreshToken(newUser);

                    newUser.setAccessToken(accessToken);
                    newUser.setRefreshToken(refreshToken);

                    return personRepository.save(newUser);
                });
    }


    public void deleteUser(UserDetails userDetails) {
        Person person = personRepository.findByUsername(userDetails.getUsername()).orElseThrow(() ->
                new EntityNotFoundException("User with username " + userDetails.getUsername() + " not found"));
        long id = person.getId();

        demonRepository.deleteByDemonlistPersonId(id);
        demonlistRepository.deleteByPersonId(id);
        personRepository.deleteById(id);
    }
}
