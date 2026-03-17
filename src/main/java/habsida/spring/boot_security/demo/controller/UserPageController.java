package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPageController {

    private final UserService userService;

    public UserPageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String userPage(Model model, Authentication authentication) {

        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", authentication.getAuthorities());

        return "user";
    }
}