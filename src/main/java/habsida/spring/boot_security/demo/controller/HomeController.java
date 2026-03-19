package habsida.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    // Корень сайта
    @GetMapping("/")
    public String rootRedirect(Principal principal) {
        if (principal == null) {
            return "login";
        }
        return "redirect:/admin";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}