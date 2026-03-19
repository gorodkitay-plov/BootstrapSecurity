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

import java.util.Set;

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
                         @RequestParam(value = "rolesSelected", required = false) Set<String> rolesSelected) {

        if (userService.isUsernameTaken(user.getUsername())) {
            bindingResult.rejectValue("username","","Username уже существует");
        }

        if (bindingResult.hasErrors()) return "newUser";

        if (rolesSelected != null) {
            Set<Role> roles = roleRepository.findAll().stream()
                    .filter(r -> rolesSelected.contains(r.getName()))
                    .collect(java.util.stream.Collectors.toSet());
            user.setRoles(roles);
        }

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
                         BindingResult bindingResult,
                         @RequestParam(value = "rolesSelected", required = false) Set<String> rolesSelected) {

        if (userService.isUsernameTakenForUpdate(user.getUsername(), id)) {
            bindingResult.rejectValue("username","","Username уже существует");
        }

        if (bindingResult.hasErrors()) return "editUser";

        if (rolesSelected != null) {
            Set<Role> roles = roleRepository.findAll().stream()
                    .filter(r -> rolesSelected.contains(r.getName()))
                    .collect(java.util.stream.Collectors.toSet());
            user.setRoles(roles);
        }

        userService.updateUser(id, user);
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}