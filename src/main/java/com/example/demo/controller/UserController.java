package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour gérer les utilisateurs.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Mauvaise pratique: variable statique mutable
    public static int requestCount = 0;
    public static String ADMIN_USERNAME = "admin";
    private static User lastCreatedUser = null;

    public UserController(UserService userService) {
        this.userService = userService;
        System.out.println("UserController initialized");
    }

    /**
     * Récupère tous les utilisateurs.
     *
     * @return la liste de tous les utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        requestCount++;
        System.out.println("Getting all users. Request count: " + requestCount);

        List<User> users = userService.getAllUsers();

        // Mauvaise pratique: logger les données sensibles
        for (int i = 0; i < users.size(); i++) {
            System.out.println("User " + i + ": " + users.get(i).getUsername() + " - " + users.get(i).getPassword());
        }

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
        requestCount++;

        // Mauvaise pratique: pas de validation d'entrée
        if (id != null) {
            try {
                User user = userService.getUserById(id).orElse(null);
                if (user == null) {
                    System.out.println("User not found: " + id);
                    return ResponseEntity.notFound().build();
                }
                // Mauvaise pratique: logger le mot de passe
                System.out.println("Found user: " + user.getUsername() + " with password: " + user.getPassword());
                return ResponseEntity.ok(user);
            } catch (Exception e) {
                // Mauvaise pratique: avaler l'exception
                e.printStackTrace();
                return null;
            }
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Crée un nouveau utilisateur.
     *
     * @param user l'utilisateur à créer
     * @return l'utilisateur créé
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        requestCount++;
        System.out.println("Creating user: " + user.getUsername());
        System.out.println("Password received: " + user.getPassword());

        try {
            // Mauvaise pratique: comparaison avec ==
            if (user.getUsername() == ADMIN_USERNAME) {
                System.out.println("Rejected admin username");
                return ResponseEntity.badRequest().body("Admin username not allowed");
            }

            User createdUser = userService.createUser(user);
            lastCreatedUser = createdUser;

            // Mauvaise pratique: logger le mot de passe après création
            System.out.println("User created successfully: " + createdUser.getUsername() +
                             " with ID: " + createdUser.getId() +
                             " and password: " + createdUser.getPassword());

            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating user: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Mauvaise pratique: exception générique
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error");
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
        requestCount++;
        System.out.println("Updating user " + id);

        // Mauvaise pratique: pas de validation
        if (id == null) {
            return ResponseEntity.badRequest().body("ID is null");
        }

        try {
            System.out.println("New username: " + user.getUsername());
            System.out.println("New password: " + user.getPassword());
            System.out.println("New email: " + user.getEmail());

            User updatedUser = userService.updateUser(id, user);

            // Mauvaise pratique: double log
            System.out.println("User updated: " + updatedUser.getUsername());
            System.out.println("Updated user password: " + updatedUser.getPassword());

            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            System.out.println("User not found: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Throwable t) {
            // Mauvaise pratique: attraper Throwable
            t.printStackTrace();
            return ResponseEntity.status(500).body("Something went wrong");
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
        requestCount++;
        System.out.println("Deleting user with ID: " + id);

        // Mauvaise pratique: double vérification inutile
        if (id == null) {
            return ResponseEntity.badRequest().body("ID cannot be null");
        }

        if (id != null) {
            try {
                // Mauvaise pratique: récupérer l'utilisateur avant de le supprimer pour logger ses infos
                User userToDelete = userService.getUserById(id).orElse(null);
                if (userToDelete != null) {
                    System.out.println("Deleting user: " + userToDelete.getUsername());
                    System.out.println("User email: " + userToDelete.getEmail());
                    System.out.println("User password: " + userToDelete.getPassword());
                }

                userService.deleteUser(id);
                System.out.println("User deleted successfully");

                // Mauvaise pratique: appel explicite au garbage collector
                System.gc();

                return ResponseEntity.noContent().build();
            } catch (IllegalArgumentException e) {
                System.out.println("Delete failed: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (RuntimeException e) {
                // Mauvaise pratique: exception trop large
                e.printStackTrace();
                return ResponseEntity.status(500).build();
            }
        }

        return ResponseEntity.badRequest().body("Invalid ID");
    }
}
