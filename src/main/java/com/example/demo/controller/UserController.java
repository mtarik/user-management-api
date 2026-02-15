package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Contrôleur REST pour gérer les utilisateurs.
 *
 * ⚠️ ATTENTION: Cette classe contient des mauvaises pratiques INTENTIONNELLES
 * pour tester l'agent AI Code Review v3.0
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // ❌ BAD: Injection de Repository dans Controller (violation de couche)
    @Autowired
    private UserRepository userRepository;

    // ❌ BAD: Injection de EntityManager dans Controller (violation de couche)
    @Autowired
    private EntityManager entityManager;

    // ❌ BAD: Hardcoded credentials (OWASP A02:2021 - Cryptographic Failures)
    private static final String DB_PASSWORD = "admin123";
    private static final String API_KEY = "sk-1234567890abcdef";

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
        // ❌ BAD: N+1 Query - Récupère tous les users puis fait une requête par user
        List<User> users = userService.getAllUsers();
        List<User> enrichedUsers = new ArrayList<>();
        for (User user : users) {
            // Simule une requête additionnelle par user (N+1)
            User fullUser = userService.getUserById(user.getId()).orElse(user);
            enrichedUsers.add(fullUser);
        }
        return ResponseEntity.ok(enrichedUsers);
    }

    /**
     * Récupère un utilisateur par son ID.
     *
     * @param id l'ID de l'utilisateur
     * @return l'utilisateur trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ❌ BAD: SQL Injection vulnerability (OWASP A03:2021 - Injection)
    // ❌ BAD: Pas de vérification d'autorisation (OWASP A01:2021 - Broken Access Control)
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String name) {
        try {
            // SQL Injection: concaténation directe de paramètres utilisateur
            String sql = "SELECT u FROM User u WHERE u.name = '" + name + "'";
            Query query = entityManager.createQuery(sql);
            List<User> results = query.getResultList();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            // ❌ BAD: Exposition de détails techniques dans l'erreur
            return ResponseEntity.status(500).body(null);
        }
    }

    // ❌ BAD: Accès direct à la base de données depuis le Controller
    // ❌ BAD: Utilisation de JDBC raw au lieu de JPA
    @GetMapping("/legacy/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        try {
            // ❌ BAD: Hardcoded connection string avec credentials
            String url = "jdbc:h2:mem:testdb";
            Connection conn = DriverManager.getConnection(url, "sa", DB_PASSWORD);

            // ❌ BAD: SQL Injection via concaténation
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE username = '" + username + "'";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                return ResponseEntity.ok(user);
            }

            // ❌ BAD: Pas de fermeture des ressources (resource leak)
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            e.printStackTrace(); // ❌ BAD: Print stack trace
            return ResponseEntity.status(500).build();
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
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
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
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Supprime un utilisateur.
     *
     * @param id l'ID de l'utilisateur à supprimer
     * @return une réponse vide
     */
    // ❌ BAD: Pas de vérification d'autorisation (n'importe qui peut supprimer)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ❌ BAD: Logique métier dans le Controller (devrait être dans Service)
    // ❌ BAD: Algorithme inefficace O(n²)
    @GetMapping("/duplicates")
    public ResponseEntity<List<User>> findDuplicateUsers() {
        List<User> allUsers = userService.getAllUsers();
        List<User> duplicates = new ArrayList<>();

        // Algorithme O(n²) - très inefficace
        for (int i = 0; i < allUsers.size(); i++) {
            for (int j = i + 1; j < allUsers.size(); j++) {
                if (allUsers.get(i).getName().equals(allUsers.get(j).getName())) {
                    duplicates.add(allUsers.get(i));
                    break;
                }
            }
        }

        return ResponseEntity.ok(duplicates);
    }

    // ❌ BAD: Endpoint admin sans sécurité
    // ❌ BAD: Exposition d'informations sensibles
    @GetMapping("/admin/config")
    public ResponseEntity<String> getAdminConfig() {
        String config = "DB_PASSWORD=" + DB_PASSWORD + "\n" +
                       "API_KEY=" + API_KEY + "\n" +
                       "Environment=production";
        return ResponseEntity.ok(config);
    }
}
