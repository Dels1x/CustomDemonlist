package ua.delsix.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.delsix.dto.DiscordUserDto;
import ua.delsix.dto.GoogleUserDto;
import ua.delsix.enums.OAuth2Type;
import ua.delsix.exception.EmailAlreadyExistsException;
import ua.delsix.exception.UsernameAlreadyExistsException;
import ua.delsix.jpa.entity.Person;
import ua.delsix.jpa.repository.DemonRepository;
import ua.delsix.jpa.repository.DemonlistRepository;
import ua.delsix.jpa.repository.PersonRepository;
import ua.delsix.mapper.PersonMapper;
import ua.delsix.util.JwtUtil;
import ua.delsix.util.CookieUtil;

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

    public void createUser(Person person) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        if (personRepository.existsByUsername(person.getUsername())) {
            String errorMessage = String.format("User %s already exists", person.getUsername());
            log.info(errorMessage);
            throw new UsernameAlreadyExistsException(errorMessage);
        }
        if (personRepository.existsByEmail(person.getEmail())) {
            String errorMessage = String.format("User %s with email %s that already exists", person.getUsername(), person.getEmail());
            log.info(errorMessage);
            throw new EmailAlreadyExistsException(errorMessage);
        }

        person.setCreatedAt(Instant.now());
        personRepository.save(person);
        log.info("New user {} created", person.getUsername());
    }

    @Transactional
    public Person createUserByDiscordDto(DiscordUserDto userDto, HttpServletResponse response, final OAuth2Type type) {
        return personRepository.findByEmail(userDto.getEmail())
                .map(existingPerson -> {
                    existingPerson.setUsername(userDto.getUsername());
                    existingPerson.setEmail(userDto.getEmail());
                    existingPerson.setPfpUrl(String.format("https://cdn.discordapp.com/avatars/%s/%s.png",
                            userDto.getId(), userDto.getAvatar()));
                    existingPerson = personRepository.save(existingPerson);

                    CookieUtil.attachAuthCookies(
                            response,
                            jwtUtil.generateAccessToken(existingPerson),
                            jwtUtil.generateRefreshToken(existingPerson));

                    return personRepository.save(existingPerson);
                })
                .orElseGet(() -> {
                    Person newPerson = PersonMapper.INSTANCE.toEntity(userDto);

                    newPerson = personRepository.save(newPerson);

                    CookieUtil.attachAuthCookies(
                            response,
                            jwtUtil.generateAccessToken(newPerson),
                            jwtUtil.generateRefreshToken(newPerson));

                    return personRepository.save(newPerson);
                });
    }

    @Transactional
    public Person createUserByGoogleDto(GoogleUserDto userDto, HttpServletResponse response, final OAuth2Type type) {
        return personRepository.findByEmail(userDto.getEmail())
                .map(existingPerson -> {
                    existingPerson.setUsername(userDto.getName());
                    existingPerson.setEmail(userDto.getEmail());

                    existingPerson.setPfpUrl(userDto.getPicture());

                    existingPerson = personRepository.save(existingPerson);

                    CookieUtil.attachAuthCookies(
                            response,
                            jwtUtil.generateAccessToken(existingPerson),
                            jwtUtil.generateRefreshToken(existingPerson));

                    return personRepository.save(existingPerson);
                })
                .orElseGet(() -> {
                    Person newPerson = PersonMapper.INSTANCE.toEntity(userDto);

                    newPerson = personRepository.save(newPerson);

                    CookieUtil.attachAuthCookies(
                            response,
                            jwtUtil.generateAccessToken(newPerson),
                            jwtUtil.generateRefreshToken(newPerson));

                    return personRepository.save(newPerson);
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
