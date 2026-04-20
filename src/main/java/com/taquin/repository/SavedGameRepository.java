package com.taquin.repository;

import com.taquin.model.Player;
import com.taquin.model.SavedGame;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class SavedGameRepository {

    @Autowired
    private SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    public void save(SavedGame game) {
        session().saveOrUpdate(game);
    }

    public Optional<SavedGame> findByPlayer(Player player) {
        return session()
                .createQuery(
                        "FROM SavedGame sg WHERE sg.player = :player ORDER BY sg.savedAt DESC",
                        SavedGame.class)
                .setParameter("player", player)
                .setMaxResults(1)
                .uniqueResultOptional();
    }

    public void deleteByPlayer(Player player) {
        session()
                .createQuery("DELETE FROM SavedGame sg WHERE sg.player = :player")
                .setParameter("player", player)
                .executeUpdate();
    }
}