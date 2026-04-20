package com.taquin.service;

import com.taquin.model.Player;
import com.taquin.model.SavedGame;
import com.taquin.repository.SavedGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class GameService {

    @Autowired
    private SavedGameRepository savedGameRepository;

    public int getSizeForLevel(int level) {
        return level + 2; // 1→3x3, 2→4x4, 3→5x5
    }

    public int[] generateBoard(int level) {
        int size = getSizeForLevel(level);
        int total = size * size;
        int[] board = new int[total];
        for (int i = 0; i < total; i++)
            board[i] = i;
        Random rand = new Random();
        do {
            for (int i = total - 1; i > 0; i--) {
                int j = rand.nextInt(i + 1);
                int tmp = board[i];
                board[i] = board[j];
                board[j] = tmp;
            }
        } while (!isSolvable(board, size));
        return board;
    }

    public boolean isSolvable(int[] board, int size) {
        int inversions = 0;
        int blankRow = 0;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) {
                blankRow = i / size;
                continue;
            }
            for (int j = i + 1; j < board.length; j++) {
                if (board[j] != 0 && board[i] > board[j])
                    inversions++;
            }
        }
        if (size % 2 == 1)
            return inversions % 2 == 0;
        int blankFromBottom = size - blankRow;
        if (blankFromBottom % 2 == 0)
            return inversions % 2 == 1;
        else
            return inversions % 2 == 0;
    }

    public int[] move(int[] board, int clickedIndex, int size) {
        int blankIndex = -1;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) {
                blankIndex = i;
                break;
            }
        }
        int row = clickedIndex / size, col = clickedIndex % size;
        int bRow = blankIndex / size, bCol = blankIndex % size;
        boolean adjacent = (row == bRow && Math.abs(col - bCol) == 1)
                || (col == bCol && Math.abs(row - bRow) == 1);
        if (!adjacent)
            return null;
        int[] newBoard = Arrays.copyOf(board, board.length);
        newBoard[blankIndex] = newBoard[clickedIndex];
        newBoard[clickedIndex] = 0;
        return newBoard;
    }

    public boolean isWon(int[] board) {
        for (int i = 0; i < board.length - 1; i++) {
            if (board[i] != i + 1)
                return false;
        }
        return board[board.length - 1] == 0;
    }

    public int calculateScore(int level, int moves) {
        int base = level * 1000;
        return Math.max(0, base - (moves * 5));
    }

    public String boardToString(int[] board) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < board.length; i++) {
            if (i > 0)
                sb.append(",");
            sb.append(board[i]);
        }
        return sb.toString();
    }

    public int[] stringToBoard(String s) {
        String[] parts = s.split(",");
        int[] board = new int[parts.length];
        for (int i = 0; i < parts.length; i++)
            board[i] = Integer.parseInt(parts[i].trim());
        return board;
    }

    public void saveGame(Player player, int[] board, int level, int moves, int score) {
        SavedGame sg = savedGameRepository.findByPlayer(player)
                .orElse(new SavedGame());
        sg.setPlayer(player);
        sg.setBoardState(boardToString(board));
        sg.setLevel(level);
        sg.setMoves(moves);
        sg.setScore(score);
        savedGameRepository.save(sg);
    }

    public Optional<SavedGame> loadGame(Player player) {
        return savedGameRepository.findByPlayer(player);
    }
}