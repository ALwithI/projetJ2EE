package com.taquin.controller;

import com.taquin.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private PlayerService playerService;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        if (session.getAttribute("playerId") == null)
            return "redirect:/login";
        model.addAttribute("username", session.getAttribute("username"));
        return "home";
    }

    @GetMapping("/leaderboard")
    public String leaderboard(Model model, HttpSession session) {
        if (session.getAttribute("playerId") == null)
            return "redirect:/login";
        model.addAttribute("players", playerService.getLeaderboard());
        return "leaderboard";
    }
}