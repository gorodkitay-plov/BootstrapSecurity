package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.model.Role;
import habsida.spring.boot_security.demo.model.User;
import habsida.spring.boot_security.demo.model.UserCreateDto;
import habsida.spring.boot_security.demo.model.UserEditDto;
import habsida.spring.boot_security.demo.repository.RoleRepository;
import habsida.spring.boot_security.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String users(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("rolesList", roleRepository.findAll());
        return "users";
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new UserCreateDto());
        model.addAttribute("rolesList", roleRepository.findAll());
        return "newUser";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("user") UserCreateDto dto,
                         BindingResult bindingResult,
                         Model model) {

        if (userService.isUsernameTaken(dto.getUsername())) {
            bindingResult.rejectValue("username", "", "Username уже существует");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("rolesList", roleRepository.findAll());
            return "newUser";
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
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String editUser(Model model, @PathVariable Long id) {
        model.addAttribute("user", userService.getUser(id));
        model.addAttribute("rolesList", roleRepository.findAll());
        return "editUser";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("user") User user,
                         @RequestParam(value = "rolesSelected", required = false)
                         Set<String> rolesSelected) {

        if (userService.isUsernameTakenForUpdate(user.getUsername(), id)) {
            return "redirect:/admin/" + id + "/edit";
        }

        if (rolesSelected == null || rolesSelected.isEmpty()) {
            return "redirect:/admin/" + id + "/edit";
        }

        Set<Role> roles = roleRepository.findAll().stream()
                .filter(r -> rolesSelected.contains(r.getName()))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        userService.updateUser(id, user);
        return "redirect:/admin";
    }

    @PatchMapping("/{id}/ajax")
    @ResponseBody
    public Map<String, Object> updateUserAjax(@PathVariable Long id,
                                              @Valid @RequestBody UserEditDto dto,
                                              BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();

        if (userService.isUsernameTakenForUpdate(dto.getUsername(), id)) {
            bindingResult.rejectValue("username", "", "Username уже существует");
        }

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            f -> f.getField(),
                            f -> f.getDefaultMessage() != null ? f.getDefaultMessage() : "Ошибка"
                    ));
            response.put("status", "error");
            response.put("errors", errors);
            return response;
        }

        User user = userService.getUser(id);

        if (user == null) {
            response.put("status", "error");
            response.put("message", "Пользователь не найден");
            return response;
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

        response.put("status", "success");
        return response;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}