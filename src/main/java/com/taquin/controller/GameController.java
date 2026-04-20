package com.taquin.controller;

import com.taquin.model.Player;
import com.taquin.model.SavedGame;
import com.taquin.service.GameService;
import com.taquin.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;
    @Autowired
    private PlayerService playerService;

    @GetMapping("/new")
    public String newGame(@RequestParam(defaultValue = "1") int level,
            HttpSession session, Model model) {
        if (session.getAttribute("playerId") == null)
            return "redirect:/login";
        int[] board = gameService.generateBoard(level);
        session.setAttribute("board", gameService.boardToString(board));
        session.setAttribute("level", level);
        session.setAttribute("moves", 0);
        session.setAttribute("score", 0);
        return buildModel(model, session, board, level, false);
    }

    @GetMapping("/load")
    public String loadGame(HttpSession session, Model model) {
        Long pid = (Long) session.getAttribute("playerId");
        if (pid == null)
            return "redirect:/login";
        Player player = playerService.findById(pid).orElseThrow();
        Optional<SavedGame> sg = gameService.loadGame(player);
        if (sg.isEmpty())
            return "redirect:/home";
        int[] board = gameService.stringToBoard(sg.get().getBoardState());
        int level = sg.get().getLevel();
        session.setAttribute("board", sg.get().getBoardState());
        session.setAttribute("level", level);
        session.setAttribute("moves", sg.get().getMoves());
        session.setAttribute("score", sg.get().getScore());
        return buildModel(model, session, board, level, false);
    }

    @GetMapping
    public String showGame(HttpSession session, Model model) {
        if (session.getAttribute("board") == null)
            return "redirect:/home";
        int level = (int) session.getAttribute("level");
        int[] board = gameService.stringToBoard((String) session.getAttribute("board"));
        return buildModel(model, session, board, level, false);
    }

    @PostMapping("/move")
    public String move(@RequestParam int index,
            HttpSession session, Model model) {

        if (session.getAttribute("board") == null)
            return "redirect:/home";

        int level = (int) session.getAttribute("level");
        int size = gameService.getSizeForLevel(level);
        int[] board = gameService.stringToBoard((String) session.getAttribute("board"));
        int moves = (int) session.getAttribute("moves");

        int[] newBoard = gameService.move(board, index, size);
        boolean won = false;

        if (newBoard != null) {
            moves++;
            session.setAttribute("board", gameService.boardToString(newBoard));
            session.setAttribute("moves", moves);
            board = newBoard;

            if (gameService.isWon(newBoard)) {
                won = true;

                int score = gameService.calculateScore(level, moves);
                session.setAttribute("score", score);

                Long pid = (Long) session.getAttribute("playerId");
                Player player = playerService.findById(pid).orElseThrow();
                playerService.updateBestScore(player, score);
            }
        }

        // ✅ IMPORTANT
        session.setAttribute("won", won);

        return buildModel(model, session, board, level, won);
    }

    @PostMapping("/save")
    public String saveGame(HttpSession session) {
        Long pid = (Long) session.getAttribute("playerId");
        if (pid == null)
            return "redirect:/login";
        Player player = playerService.findById(pid).orElseThrow();
        int[] board = gameService.stringToBoard((String) session.getAttribute("board"));
        int level = (int) session.getAttribute("level");
        int moves = (int) session.getAttribute("moves");
        int score = (int) session.getAttribute("score");
        gameService.saveGame(player, board, level, moves, score);
        return "redirect:/game?saved=true";
    }

    @PostMapping("/next")
    public String nextLevel(HttpSession session) {

        Boolean won = (Boolean) session.getAttribute("won");

        if (won == null || !won) {
            return "redirect:/game";
        }

        Integer level = (Integer) session.getAttribute("level");

        if (level == null)
            return "redirect:/home";

        // max niveau
        if (level >= 3) {
            return "redirect:/home?finished=true";
        }

        level++;

        int[] board = gameService.generateBoard(level);

        session.setAttribute("board", gameService.boardToString(board));
        session.setAttribute("level", level);
        session.setAttribute("moves", 0);
        session.setAttribute("score", 0);
        session.setAttribute("won", false); // reset

        Long pid = (Long) session.getAttribute("playerId");
        Player player = playerService.findById(pid).orElseThrow();
        playerService.updateLevel(player, level);

        return "redirect:/game";
    }

    private String buildModel(Model model, HttpSession session,
            int[] board, int level, boolean won) {
        model.addAttribute("board", board);
        model.addAttribute("size", gameService.getSizeForLevel(level));
        model.addAttribute("level", level);
        model.addAttribute("moves", session.getAttribute("moves"));
        model.addAttribute("score", session.getAttribute("score"));
        model.addAttribute("won", won);
        model.addAttribute("username", session.getAttribute("username"));
        return "game";
    }
}