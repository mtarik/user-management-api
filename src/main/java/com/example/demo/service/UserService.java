package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ⚠️ SERVICE AVEC MAUVAISES PRATIQUES - POUR TEST AI REVIEW
 */
@Service
@Transactional
public class UserService {

    // ❌ MAUVAISE PRATIQUE: Credentials hardcodées
    private static final String DB_URL = "jdbc:mysql://localhost:3306/userdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "SuperSecret123!";
    private static final String API_KEY = "sk-1234567890abcdef";
    private static final String ADMIN_PASSWORD = "admin123";

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ❌ MAUVAISE PRATIQUE: Pas de validation, logique métier faible
    public User createUser(User user) {
        // Pas de validation du tout
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        // ❌ MAUVAISE PRATIQUE: Pas de validation
        return userRepository.findById(id);
    }

    // ❌ MAUVAISE PRATIQUE: SQL Injection vulnérable
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();

            // SQL Injection vulnérable !
            String query = "SELECT * FROM users WHERE username = '" + username + "'";
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                return Optional.of(user);
            }

            // ❌ MAUVAISE PRATIQUE: Resource leak - connexion jamais fermée
            return Optional.empty();

        } catch (Exception e) {
            // ❌ MAUVAISE PRATIQUE: printStackTrace au lieu de logger
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // ❌ MAUVAISE PRATIQUE: Logique métier faible, pas de validation
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur avec l'ID " + id + " non trouvé"));

        // ❌ Pas de validation des données
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());

        return userRepository.save(user);
    }

    // ❌ MAUVAISE PRATIQUE: Pas de vérification d'existence
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // ❌ MAUVAISE PRATIQUE: SQL Injection + Resource leak
    @Transactional(readOnly = true)
    public List<User> searchUsersByName(String name) {
        List<User> results = new ArrayList<>();

        try {
            // ❌ Resource leak: connexion jamais fermée
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();

            // ❌ SQL Injection vulnérable !
            String sql = "SELECT * FROM users WHERE name LIKE '%" + name + "%'";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                results.add(user);
            }

            // ❌ Pas de fermeture des ressources

        } catch (Exception e) {
            // ❌ MAUVAISE PRATIQUE: printStackTrace
            e.printStackTrace();
            System.out.println("Erreur SQL: " + e.getMessage());
        }

        return results;
    }

    // ❌ MAUVAISE PRATIQUE: Algorithme O(n²) très inefficace
    @Transactional(readOnly = true)
    public List<User> findDuplicateUsers() {
        List<User> allUsers = userRepository.findAll();
        List<User> duplicates = new ArrayList<>();

        // ❌ Algorithme O(n²) - très inefficace !
        for (int i = 0; i < allUsers.size(); i++) {
            for (int j = i + 1; j < allUsers.size(); j++) {
                if (allUsers.get(i).getName().equals(allUsers.get(j).getName())) {
                    if (!duplicates.contains(allUsers.get(i))) {
                        duplicates.add(allUsers.get(i));
                    }
                    if (!duplicates.contains(allUsers.get(j))) {
                        duplicates.add(allUsers.get(j));
                    }
                }
            }
        }

        return duplicates;
    }

    // ❌ MAUVAISE PRATIQUE: Méthode exposant des informations sensibles
    public String getSystemInfo() {
        return "DB_URL=" + DB_URL +
               "\nDB_USER=" + DB_USER +
               "\nDB_PASSWORD=" + DB_PASSWORD +
               "\nAPI_KEY=" + API_KEY +
               "\nADMIN_PASSWORD=" + ADMIN_PASSWORD;
    }

    // ❌ MAUVAISE PRATIQUE: SQL Injection + Hardcoded credentials + Resource leak
    public boolean authenticateUser(String username, String password) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();

            // ❌ Triple problème: SQL injection + plain text password + resource leak
            String query = "SELECT * FROM users WHERE username = '" + username +
                          "' AND password = '" + password + "'";
            ResultSet rs = stmt.executeQuery(query);

            boolean authenticated = rs.next();

            // ❌ Resource leak - jamais fermé
            return authenticated;

        } catch (Exception e) {
            // ❌ Exposition d'informations sensibles dans les logs
            System.err.println("Auth failed for user: " + username + " with password: " + password);
            e.printStackTrace();
            return false;
        }
    }

    // ❌ MAUVAISE PRATIQUE: Pas de validation d'email
    public User createUserWithEmail(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);  // Pas de validation du format email
        user.setPassword(password);  // Pas de hashage du mot de passe

        return userRepository.save(user);
    }

    // ❌ MAUVAISE PRATIQUE: Logique métier complexe sans tests
    public void promoteToAdmin(Long userId, String secretCode) {
        if (secretCode.equals(ADMIN_PASSWORD)) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                // ❌ Modification directe sans validation
                user.setUsername("admin_" + user.getUsername());
                userRepository.save(user);
            }
        }
    }

    // ❌ MAUVAISE PRATIQUE: Méthode qui fait trop de choses (God Method)
    public String processUserData(Long id, String action, String data) {
        try {
            User user = userRepository.findById(id).orElse(null);

            if (action.equals("update_email")) {
                user.setEmail(data);
                userRepository.save(user);
                return "Email updated";
            } else if (action.equals("update_password")) {
                user.setPassword(data);  // Pas de hashage
                userRepository.save(user);
                return "Password updated";
            } else if (action.equals("delete")) {
                userRepository.deleteById(id);
                return "User deleted";
            } else if (action.equals("export")) {
                // ❌ Exposition de données sensibles
                return "User: " + user.getUsername() + ", Password: " + user.getPassword();
            }

            return "Unknown action";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
