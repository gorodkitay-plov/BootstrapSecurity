package habsida.spring.boot_security.demo.configs;

import habsida.spring.boot_security.demo.repository.RoleRepository;
import habsida.spring.boot_security.demo.model.Role;
import habsida.spring.boot_security.demo.model.User;
import habsida.spring.boot_security.demo.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Set;

@Configuration
public class WebSecurityConfig {

    private final SuccessUserHandler successUserHandler;

    public WebSecurityConfig(SuccessUserHandler successUserHandler) {
        this.successUserHandler = successUserHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successUserHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner init(UserService userService,
                                  RoleRepository roleRepository,
                                  PasswordEncoder passwordEncoder) {
        return args -> {
            if (roleRepository.findByName("ROLE_ADMIN") == null) {
                roleRepository.save(new Role("ROLE_ADMIN"));
            }
            if (roleRepository.findByName("ROLE_USER") == null) {
                roleRepository.save(new Role("ROLE_USER"));
            }
            if (userService.findByUsername("admin") == null) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setName("Admin");
                admin.setSurname("Adminov");
                admin.setAge(30);
                admin.setEmail("admin@mail.com");
                Role adminRole = roleRepository.findByName("ROLE_ADMIN");
                admin.setRoles(Set.of(adminRole));
                userService.saveUser(admin);
            }
        };
    }
}