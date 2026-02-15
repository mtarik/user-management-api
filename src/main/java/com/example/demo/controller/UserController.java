package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour gérer les utilisateurs.
 *
 * ✅ Version corrigée - Toutes les mauvaises pratiques ont été éliminées
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Récupère tous les utilisateurs.
     *
     * @return la liste de tous les utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.debug("Récupération de tous les utilisateurs");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Récupère un utilisateur par son ID.
     *
     * @param id l'ID de l'utilisateur
     * @return l'utilisateur trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.debug("Récupération de l'utilisateur avec l'ID: {}", id);
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Recherche des utilisateurs par nom.
     *
     * @param name le nom à rechercher
     * @return la liste des utilisateurs correspondants
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String name) {
        logger.debug("Recherche d'utilisateurs avec le nom: {}", name);
        try {
            List<User> results = userService.searchUsersByName(name);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche d'utilisateurs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Recherche un utilisateur par nom d'utilisateur.
     *
     * @param username le nom d'utilisateur
     * @return l'utilisateur trouvé
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        logger.debug("Recherche de l'utilisateur: {}", username);
        try {
            return userService.getUserByUsername(username)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche par username", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crée un nouveau utilisateur.
     *
     * @param user l'utilisateur à créer
     * @return l'utilisateur créé
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        logger.info("Création d'un nouvel utilisateur: {}", user.getName());
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            logger.warn("Validation échouée pour la création d'utilisateur: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Met à jour un utilisateur existant.
     *
     * @param id l'ID de l'utilisateur à mettre à jour
     * @param user les nouvelles informations de l'utilisateur
     * @return l'utilisateur mis à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        logger.info("Mise à jour de l'utilisateur avec l'ID: {}", id);
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            logger.warn("Utilisateur non trouvé pour mise à jour: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Supprime un utilisateur.
     *
     * @param id l'ID de l'utilisateur à supprimer
     * @return une réponse vide
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("Suppression de l'utilisateur avec l'ID: {}", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Utilisateur non trouvé pour suppression: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Trouve les utilisateurs en double (même nom).
     *
     * @return la liste des utilisateurs en double
     */
    @GetMapping("/duplicates")
    public ResponseEntity<List<User>> findDuplicateUsers() {
        logger.debug("Recherche d'utilisateurs en double");
        List<User> duplicates = userService.findDuplicateUsers();
        return ResponseEntity.ok(duplicates);
    }
}
