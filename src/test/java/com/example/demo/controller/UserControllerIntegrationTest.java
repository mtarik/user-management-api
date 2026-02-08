package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour UserController avec une vraie base de données H2.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController Integration Tests")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Nettoyer la base avant chaque test
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
    }

    @AfterEach
    void tearDown() {
        // Nettoyer la base après chaque test
        userRepository.deleteAll();
    }

    // ========== Tests GET /api/users ==========

    @Test
    @DisplayName("GET /api/users - Retourne tous les utilisateurs")
    void getAllUsers_ReturnsAllUsers() throws Exception {
        // Créer plusieurs utilisateurs en base
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        userRepository.save(user2);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].username", containsInAnyOrder("user1", "user2")));
    }

    @Test
    @DisplayName("GET /api/users - Retourne liste vide si aucun utilisateur")
    void getAllUsers_EmptyDatabase_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ========== Tests GET /api/users/{id} ==========

    @Test
    @DisplayName("GET /api/users/{id} - Retourne utilisateur existant")
    void getUserById_ExistingUser_ReturnsUser() throws Exception {
        User savedUser = userRepository.save(testUser);

        mockMvc.perform(get("/api/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(savedUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Retourne 404 pour utilisateur inexistant")
    void getUserById_NonExistingUser_Returns404() throws Exception {
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    // ========== Tests POST /api/users ==========

    @Test
    @DisplayName("POST /api/users - Crée un nouvel utilisateur")
    void createUser_ValidUser_CreatesAndReturnsUser() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));

        // Vérifier que l'utilisateur est bien en base
        assert userRepository.findByUsername("testuser").isPresent();
    }

    @Test
    @DisplayName("POST /api/users - Rejette utilisateur avec username 'admin'")
    void createUser_AdminUsername_ReturnsBadRequest() throws Exception {
        testUser.setUsername("admin");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("admin")));

        // Vérifier que l'utilisateur n'est pas en base
        assert userRepository.findByUsername("admin").isEmpty();
    }

    @Test
    @DisplayName("POST /api/users - Rejette utilisateur avec données invalides")
    void createUser_InvalidData_ReturnsBadRequest() throws Exception {
        User invalidUser = new User();
        invalidUser.setEmail("invalid-email"); // Email invalide
        // username et password manquants

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    // ========== Tests PUT /api/users/{id} ==========

    @Test
    @DisplayName("PUT /api/users/{id} - Met à jour utilisateur existant")
    void updateUser_ExistingUser_UpdatesAndReturnsUser() throws Exception {
        User savedUser = userRepository.save(testUser);

        User updateData = new User();
        updateData.setUsername("updateduser");
        updateData.setEmail("updated@example.com");
        updateData.setPassword("newpassword");

        mockMvc.perform(put("/api/users/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(savedUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is("updateduser")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));

        // Vérifier en base
        User updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assert updatedUser.getUsername().equals("updateduser");
        assert updatedUser.getEmail().equals("updated@example.com");
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Retourne 404 pour utilisateur inexistant")
    void updateUser_NonExistingUser_Returns404() throws Exception {
        User updateData = new User();
        updateData.setUsername("updateduser");
        updateData.setEmail("updated@example.com");
        updateData.setPassword("newpassword");

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("non trouvé")));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Rejette données invalides")
    void updateUser_InvalidData_ReturnsBadRequest() throws Exception {
        User savedUser = userRepository.save(testUser);

        User invalidUpdate = new User();
        invalidUpdate.setEmail("invalid-email"); // Email invalide
        // username et password manquants

        mockMvc.perform(put("/api/users/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    // ========== Tests DELETE /api/users/{id} ==========

    @Test
    @DisplayName("DELETE /api/users/{id} - Supprime utilisateur existant")
    void deleteUser_ExistingUser_DeletesUser() throws Exception {
        User savedUser = userRepository.save(testUser);
        Long userId = savedUser.getId();

        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());

        // Vérifier que l'utilisateur n'est plus en base
        assert userRepository.findById(userId).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Retourne 404 pour utilisateur inexistant")
    void deleteUser_NonExistingUser_Returns404() throws Exception {
        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("non trouvé")));
    }

    // ========== Tests de scénarios complexes ==========

    @Test
    @DisplayName("Scénario complet - CRUD sur un utilisateur")
    void fullCrudScenario_WorksCorrectly() throws Exception {
        // 1. Créer un utilisateur
        String createResponse = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        User createdUser = objectMapper.readValue(createResponse, User.class);
        Long userId = createdUser.getId();

        // 2. Lire l'utilisateur créé
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")));

        // 3. Mettre à jour l'utilisateur
        User updateData = new User();
        updateData.setUsername("updateduser");
        updateData.setEmail("updated@example.com");
        updateData.setPassword("newpassword");

        mockMvc.perform(put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("updateduser")));

        // 4. Vérifier la mise à jour
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("updateduser")));

        // 5. Supprimer l'utilisateur
        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());

        // 6. Vérifier la suppression
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Scénario - Liste des utilisateurs après opérations CRUD")
    void listUsersAfterOperations_ReflectsChanges() throws Exception {
        // Vérifier liste vide
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // Créer 3 utilisateurs
        for (int i = 1; i <= 3; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@example.com");
            user.setPassword("password" + i);
            userRepository.save(user);
        }

        // Vérifier qu'on a 3 utilisateurs
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        // Supprimer un utilisateur
        User userToDelete = userRepository.findByUsername("user2").orElseThrow();
        mockMvc.perform(delete("/api/users/" + userToDelete.getId()))
                .andExpect(status().isNoContent());

        // Vérifier qu'il reste 2 utilisateurs
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].username", containsInAnyOrder("user1", "user3")));
    }
}
