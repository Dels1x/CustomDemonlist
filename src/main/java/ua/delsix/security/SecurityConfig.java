package ua.delsix.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ua.delsix.jpa.entity.Person;
import ua.delsix.jpa.repository.PersonRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final PersonRepository personRepository;

    public SecurityConfig(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/users/create").permitAll()
                        .requestMatchers("/oauth2/callback/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2 // Discord
                        .loginPage("/oauth2/callback/discord")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Person person = personRepository.findOptionalByUsername(username).orElseThrow(() ->
                    new UsernameNotFoundException("User not found with username: " + username));

            return org.springframework.security.core.userdetails.User.withUsername(person.getUsername())
                    .build();
        };
    }
}
