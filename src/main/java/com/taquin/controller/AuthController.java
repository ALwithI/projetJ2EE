package com.taquin.controller;

import com.taquin.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private PlayerService playerService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
            @RequestParam String password,
            HttpSession session, Model model) {
        var player = playerService.login(username, password);
        if (player.isPresent()) {
            session.setAttribute("playerId", player.get().getId());
            session.setAttribute("username", player.get().getUsername());
            return "redirect:/home";
        }
        model.addAttribute("error", "Identifiants incorrects.");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
            @RequestParam String password,
            Model model) {
        if (username.isBlank() || password.length() < 4) {
            model.addAttribute("error", "Mot de passe minimum 4 caractères.");
            return "register";
        }
        boolean ok = playerService.register(username, password);
        if (!ok) {
            model.addAttribute("error", "Nom d'utilisateur déjà pris.");
            return "register";
        }
        return "redirect:/login?registered=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}