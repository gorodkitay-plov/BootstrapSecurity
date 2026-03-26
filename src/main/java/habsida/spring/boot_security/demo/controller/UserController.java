package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.model.Role;
import habsida.spring.boot_security.demo.model.User;
import habsida.spring.boot_security.demo.repository.RoleRepository;
import habsida.spring.boot_security.demo.service.UserService;
import jakarta.validation.Valid;
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

    public UserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String users(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("rolesList", roleRepository.findAll());
        return "users";
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("rolesList", roleRepository.findAll());
        return "newUser";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("user") User user,
                         BindingResult bindingResult,
                         @RequestParam(value = "rolesSelected", required = false) Set<String> rolesSelected,
                         Model model) {

        if (userService.isUsernameTaken(user.getUsername())) {
            bindingResult.rejectValue("username","","Username уже существует");
        }

        if (rolesSelected == null || rolesSelected.isEmpty()) {
            bindingResult.rejectValue("roles", "", "Выберите хотя бы одну роль");
        }

        if (bindingResult.hasErrors()){
            model.addAttribute("rolesList", roleRepository.findAll());
            return "newUser";
        }

        Set<Role> roles = roleRepository.findAll().stream()
                .filter(r -> rolesSelected.contains(r.getName()))
                .collect(java.util.stream.Collectors.toSet());

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
                         @ModelAttribute("user") User user,
                         @RequestParam(value = "rolesSelected", required = false)
                         Set<String> rolesSelected) {

        // username check
        if (userService.isUsernameTakenForUpdate(user.getUsername(), id)) {
            return "redirect:/admin";
        }

        // roles check
        if (rolesSelected == null || rolesSelected.isEmpty()) {
            return "redirect:/admin";
        }

        Set<Role> roles = roleRepository.findAll().stream()
                .filter(r -> rolesSelected.contains(r.getName()))
                .collect(java.util.stream.Collectors.toSet());

        user.setRoles(roles);

        userService.updateUser(id, user);

        return "redirect:/admin";
    }

    @PatchMapping("/{id}/ajax")
    @ResponseBody
    public Map<String, Object> updateAjax(@PathVariable Long id,
                                          @RequestBody Map<String, Object> body) {

        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        // получаем поля из JSON
        String username = (String) body.get("username");
        String name = (String) body.get("name");
        String surname = (String) body.get("surname");
        String email = (String) body.get("email");

        Integer age = null;
        try {
            Object ageObj = body.get("age");
            if (ageObj != null) {
                age = Integer.valueOf(ageObj.toString());
            }
        } catch (NumberFormatException e) {
            errors.put("age", "Возраст должен быть числом");
        }

        List<String> rolesSelected = (List<String>) body.get("rolesSelected");

        // Валидация
        if (username == null || username.isBlank()) {
            errors.put("username", "Username обязателен");
        } else if (userService.isUsernameTakenForUpdate(username, id)) {
            errors.put("username", "Username уже существует");
        }

        if (name == null || name.isBlank()) {
            errors.put("name", "Имя обязательно");
        } else if (!name.matches("^[A-Za-zА-Яа-яЁё]+$")) {
            errors.put("name", "Имя должно содержать только буквы");
        }

        if (surname == null || surname.isBlank()) {
            errors.put("surname", "Фамилия обязательна");
        } else if (!surname.matches("^[A-Za-zА-Яа-яЁё]+$")) {
            errors.put("surname", "Фамилия должна содержать только буквы");
        }

        if (email == null || email.isBlank()) {
            errors.put("email", "Email обязателен");
        } else if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.put("email", "Некорректный email");
        }

        if (age == null) {
            errors.put("age", "Возраст обязателен");
        } else if (age < 1) {
            errors.put("age", "Возраст должен быть больше 0");
        } else if (age > 120) {
            errors.put("age", "Возраст должен быть меньше 120");
        }

        if (rolesSelected == null || rolesSelected.isEmpty()) {
            errors.put("roles", "Выберите хотя бы одну роль");
        }

        // Если есть ошибки, возвращаем их
        if (!errors.isEmpty()) {
            response.put("status", "error");
            response.put("errors", errors);
            return response;
        }

        // получаем пользователя из БД
        User existingUser = userService.getUser(id);

        existingUser.setUsername(username);
        existingUser.setName(name);
        existingUser.setSurname(surname);
        existingUser.setEmail(email);
        existingUser.setAge(age);

        // роли
        Set<Role> roles = roleRepository.findAll().stream()
                .filter(r -> rolesSelected.contains(r.getName()))
                .collect(Collectors.toSet());

        existingUser.setRoles(roles);

        userService.updateUser(id, existingUser);

        response.put("status", "success");
        return response;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}