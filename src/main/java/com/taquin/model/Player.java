package com.taquin.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "best_score")
    private int bestScore = 0;

    @Column(name = "total_games")
    private int totalGames = 0;

    @Column(name = "current_level")
    private int currentLevel = 1;
}