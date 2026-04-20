package com.taquin.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_games")
@Data
@NoArgsConstructor
public class SavedGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    // Ex : "1,2,3,4,5,6,7,8,0" pour grille 3x3
    @Column(name = "board_state", length = 200)
    private String boardState;

    private int level; // 1=3x3 | 2=4x4 | 3=5x5
    private int moves;
    private int score;

    @Column(name = "saved_at")
    private LocalDateTime savedAt;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.savedAt = LocalDateTime.now();
    }
}