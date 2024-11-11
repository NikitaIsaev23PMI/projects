package ru.nikita.CRUD.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.nikita.CRUD.services.EmailService;

@Controller
public class MainController {

    @GetMapping("/main-page")
    public String mainPage() {
        return "main/main";
    }
}
