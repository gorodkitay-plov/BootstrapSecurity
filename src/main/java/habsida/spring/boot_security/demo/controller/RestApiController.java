package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.model.Role;
import habsida.spring.boot_security.demo.model.User;
import habsida.spring.boot_security.demo.model.UserCreateDto;
import habsida.spring.boot_security.demo.model.UserEditDto;
import habsida.spring.boot_security.demo.repository.RoleRepository;
import habsida.spring.boot_security.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RestApiController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RestApiController(UserService userService,
                             RoleRepository roleRepository,
                             PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ===== ТЕКУЩИЙ ПОЛЬЗОВАТЕЛЬ =====

    // GET /api/me → данные залогиненного пользователя
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(user);
    }

    // ===== ПОЛЬЗОВАТЕЛИ (только ADMIN) =====

    // GET /api/users → список всех пользователей
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET /api/users/{id} → один пользователь по id
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // POST /api/users → создать пользователя
    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserCreateDto dto,
                                             BindingResult bindingResult) {

        if (userService.isUsernameTaken(dto.getUsername())) {
            bindingResult.rejectValue("username", "", "Username уже существует");
        }

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = collectErrors(bindingResult);
            return ResponseEntity.badRequest().body(errors);
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // saveUser сам зашифрует
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setAge(dto.getAge());

        Set<Role> roles = roleRepository.findAll().stream()
                .filter(r -> dto.getRoles().contains(r.getName()))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // PATCH /api/users/{id} → обновить пользователя
    @PatchMapping("/users/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id,
                                             @Valid @RequestBody UserEditDto dto,
                                             BindingResult bindingResult) {

        if (userService.isUsernameTakenForUpdate(dto.getUsername(), id)) {
            bindingResult.rejectValue("username", "", "Username уже существует");
        }

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = collectErrors(bindingResult);
            return ResponseEntity.badRequest().body(errors);
        }

        User user = userService.getUser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setAge(dto.getAge());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Set<Role> roles = roleRepository.findAll().stream()
                .filter(r -> dto.getRoles().contains(r.getName()))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        userService.updateUser(id, user);

        return ResponseEntity.ok(user);
    }

    // DELETE /api/users/{id} → удалить пользователя
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.getUser(id) == null) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ===== РОЛИ =====

    // GET /api/roles → список всех ролей
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

    // ===== ВСПОМОГАТЕЛЬНЫЙ МЕТОД =====
    private Map<String, String> collectErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        f -> f.getField(),
                        f -> f.getDefaultMessage() != null ? f.getDefaultMessage() : "Ошибка"
                ));
    }
}