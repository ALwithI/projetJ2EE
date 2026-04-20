package com.taquin.repository;

import com.taquin.model.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class PlayerRepository {

    @Autowired
    private SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    public void save(Player player) {
        session().saveOrUpdate(player);
    }

    public Optional<Player> findById(Long id) {
        return Optional.ofNullable(session().get(Player.class, id));
    }

    public Optional<Player> findByUsername(String username) {
        return session()
                .createQuery("FROM Player WHERE username = :u", Player.class)
                .setParameter("u", username)
                .uniqueResultOptional();
    }

    public boolean existsByUsername(String username) {
        Long count = session()
                .createQuery("SELECT COUNT(p) FROM Player p WHERE p.username = :u", Long.class)
                .setParameter("u", username)
                .uniqueResult();
        return count != null && count > 0;
    }

    public List<Player> findTop10ByBestScore() {
        return session()
                .createQuery("FROM Player ORDER BY bestScore DESC", Player.class)
                .setMaxResults(10)
                .list();
    }
}