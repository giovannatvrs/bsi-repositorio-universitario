package com.example.repositorio_universitario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    @GetMapping("/visitante")
    public String visitantePage() {
        return "visitante";
    }

}
