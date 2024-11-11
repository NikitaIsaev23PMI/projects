package ru.nikita.CRUD.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.nikita.CRUD.entity.securityEntity.LibraryUser;
import ru.nikita.CRUD.services.LoginService;
import ru.nikita.CRUD.util.UserValidator;

@Controller
public class SecurityController {

    private LoginService loginService;

    private UserValidator userValidator;

    @Autowired
    SecurityController(LoginService loginService, UserValidator userValidator) {
        this.loginService = loginService;
        this.userValidator = userValidator;
    }

    @GetMapping("/login")
    public String login() {
        return "loginPage/login";
    }

    @PostMapping("/new-user")
    public String newUser(@ModelAttribute("user") @Valid LibraryUser user, BindingResult result, Model model){

        userValidator.validate(user, result);

        if (result.hasErrors()) {
            return "loginPage/registration";
        }
        loginService.registerUser(user);
        return "loginPage/login";
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("user")LibraryUser user) {
        return "loginPage/registration";
    }
}
