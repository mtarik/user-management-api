package com.example.demo.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de validation pour le modèle User.
 */
@DisplayName("User Model Tests")
class UserTest {

    private Validator validator;
    private User validUser;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validUser = new User();
        validUser.setUsername("testuser");
        validUser.setEmail("test@example.com");
        validUser.setPassword("password123");
    }

    // ========== Tests de validation réussie ==========

    @Test
    @DisplayName("User valide - Aucune violation de contrainte")
    void validUser_NoViolations() {
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertTrue(violations.isEmpty(),
            "Un utilisateur valide ne devrait avoir aucune violation");
    }

    // ========== Tests de validation du username ==========

    @Test
    @DisplayName("Username null - Violation de contrainte NotBlank")
    void usernameNull_ViolatesNotBlank() {
        validUser.setUsername(null);

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    @DisplayName("Username vide - Violation de contrainte NotBlank")
    void usernameEmpty_ViolatesNotBlank() {
        validUser.setUsername("");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("username")
                && v.getMessage().contains("obligatoire")));
    }

    @Test
    @DisplayName("Username avec espaces seulement - Violation de contrainte NotBlank")
    void usernameBlank_ViolatesNotBlank() {
        validUser.setUsername("   ");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    // ========== Tests de validation de l'email ==========

    @Test
    @DisplayName("Email null - Violation de contrainte NotBlank")
    void emailNull_ViolatesNotBlank() {
        validUser.setEmail(null);

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Email vide - Violation de contrainte NotBlank")
    void emailEmpty_ViolatesNotBlank() {
        validUser.setEmail("");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Email invalide - Violation de contrainte Email")
    void emailInvalid_ViolatesEmail() {
        validUser.setEmail("invalid-email");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("email")
                && v.getMessage().contains("valide")));
    }

    @Test
    @DisplayName("Email valide avec domaine - Pas de violation")
    void emailValidWithDomain_NoViolations() {
        validUser.setEmail("user@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Email valide avec sous-domaine - Pas de violation")
    void emailValidWithSubdomain_NoViolations() {
        validUser.setEmail("user@mail.example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertTrue(violations.isEmpty());
    }

    // ========== Tests de validation du password ==========

    @Test
    @DisplayName("Password null - Violation de contrainte NotBlank")
    void passwordNull_ViolatesNotBlank() {
        validUser.setPassword(null);

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Password vide - Violation de contrainte NotBlank")
    void passwordEmpty_ViolatesNotBlank() {
        validUser.setPassword("");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("password")
                && v.getMessage().contains("obligatoire")));
    }

    // ========== Tests des callbacks JPA (@PrePersist, @PreUpdate) ==========

    @Test
    @DisplayName("onCreate - Initialise les timestamps createdAt et updatedAt")
    void onCreate_InitializesTimestamps() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password");

        // Simule @PrePersist
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        newUser.onCreate();
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertNotNull(newUser.getCreatedAt());
        assertNotNull(newUser.getUpdatedAt());
        assertTrue(newUser.getCreatedAt().isAfter(before));
        assertTrue(newUser.getCreatedAt().isBefore(after));
        assertEquals(newUser.getCreatedAt(), newUser.getUpdatedAt());
    }

    @Test
    @DisplayName("onUpdate - Met à jour seulement updatedAt")
    void onUpdate_UpdatesOnlyUpdatedAt() throws InterruptedException {
        User user = new User();
        user.onCreate();

        LocalDateTime originalCreatedAt = user.getCreatedAt();
        LocalDateTime originalUpdatedAt = user.getUpdatedAt();

        // Attendre un peu pour s'assurer que le timestamp change
        Thread.sleep(10);

        // Simule @PreUpdate
        user.onUpdate();

        assertEquals(originalCreatedAt, user.getCreatedAt(),
            "createdAt ne devrait pas changer lors de la mise à jour");
        assertNotEquals(originalUpdatedAt, user.getUpdatedAt(),
            "updatedAt devrait être mis à jour");
        assertTrue(user.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    // ========== Tests des getters/setters ==========

    @Test
    @DisplayName("Getters et Setters - Fonctionnent correctement")
    void gettersAndSetters_WorkCorrectly() {
        User user = new User();

        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    // ========== Tests du constructeur ==========

    @Test
    @DisplayName("Constructeur avec tous les paramètres - Initialise correctement")
    void allArgsConstructor_InitializesCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User(
            1L,
            "testuser",
            "test@example.com",
            "password123",
            now,
            now
        );

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    @DisplayName("Constructeur sans paramètres - Crée objet vide")
    void noArgsConstructor_CreatesEmptyObject() {
        User user = new User();

        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }

    // ========== Tests de validations multiples ==========

    @Test
    @DisplayName("Multiple violations - Retourne toutes les violations")
    void multipleViolations_ReturnsAllViolations() {
        User invalidUser = new User();
        // Tous les champs obligatoires sont null

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);

        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size(),
            "Devrait avoir 3 violations: username, email, et password");
    }
}
