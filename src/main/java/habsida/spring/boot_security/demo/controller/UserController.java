package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.model.User;
import habsida.spring.boot_security.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String users(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        return "newUser";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("user") User user,
                         BindingResult bindingResult) {

        if (userService.isUsernameTaken(user.getUsername())) {

            bindingResult.rejectValue(
                    "username",
                    "",
                    "Username уже существует"
            );
        }

        if (bindingResult.hasErrors()) {
            return "newUser";
        }

        userService.saveUser(user);

        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String editUser(Model model, @PathVariable("id") Long id) {
        model.addAttribute("user", userService.getUser(id));
        return "editUser";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("user") User user,
                         BindingResult bindingResult) {

        if (userService.isUsernameTakenForUpdate(user.getUsername(), id)) {

            bindingResult.rejectValue(
                    "username",
                    "",
                    "Username уже существует"
            );
        }

        if (bindingResult.hasErrors()) {
            return "editUser";
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
