package com.taquin.service;

import com.taquin.model.Player;
import com.taquin.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    public boolean register(String username, String password) {
        if (playerRepository.existsByUsername(username))
            return false;
        Player p = new Player();
        p.setUsername(username);
        p.setPassword(password);
        playerRepository.save(p);
        return true;
    }

    public Optional<Player> login(String username, String password) {
        return playerRepository.findByUsername(username)
                .filter(p -> p.getPassword().equals(password));
    }

    public void updateBestScore(Player player, int score) {
        if (score > player.getBestScore()) {
            player.setBestScore(score);
        }
        player.setTotalGames(player.getTotalGames() + 1);
        playerRepository.save(player);
    }

    public void updateLevel(Player player, int level) {
        if (level > player.getCurrentLevel()) {
            player.setCurrentLevel(level);
            playerRepository.save(player);
        }
    }

    public List<Player> getLeaderboard() {
        return playerRepository.findTop10ByBestScore();
    }

    public Optional<Player> findById(Long id) {
        return playerRepository.findById(id);
    }
}