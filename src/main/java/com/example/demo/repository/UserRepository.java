package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour gérer les opérations CRUD sur les utilisateurs.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trouve un utilisateur par son nom d'utilisateur.
     *
     * @param username le nom d'utilisateur
     * @return un Optional contenant l'utilisateur s'il existe
     */
    Optional<User> findByUsername(String username);

    /**
     * Trouve un utilisateur par son email.
     *
     * @param email l'email de l'utilisateur
     * @return un Optional contenant l'utilisateur s'il existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un utilisateur existe avec ce nom d'utilisateur.
     *
     * @param username le nom d'utilisateur
     * @return true si l'utilisateur existe
     */
    boolean existsByUsername(String username);

    /**
     * Vérifie si un utilisateur existe avec cet email.
     *
     * @param email l'email
     * @return true si l'utilisateur existe
     */
    boolean existsByEmail(String email);
}
