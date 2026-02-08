package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour UserController avec MockMvc.
 */
@WebMvcTest(UserController.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
    }

    // ========== Tests pour GET /api/users ==========

    @Test
    @DisplayName("GET /api/users - Retourne tous les utilisateurs")
    void getAllUsers_Success() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");

        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("user1")))
                .andExpect(jsonPath("$[1].username", is("user2")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("GET /api/users - Retourne liste vide si aucun utilisateur")
    void getAllUsers_EmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService, times(1)).getAllUsers();
    }

    // ========== Tests pour GET /api/users/{id} ==========

    @Test
    @DisplayName("GET /api/users/{id} - Retourne utilisateur si trouvé")
    void getUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("GET /api/users/{id} - Retourne 404 si utilisateur non trouvé")
    void getUserById_NotFound() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(1L);
    }

    // ========== Tests pour POST /api/users ==========

    @Test
    @DisplayName("POST /api/users - Crée utilisateur avec succès")
    void createUser_Success() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("POST /api/users - Retourne 400 si validation échoue")
    void createUser_ValidationFails() throws Exception {
        User invalidUser = new User();
        // Username vide devrait échouer la validation @NotBlank

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    @DisplayName("POST /api/users - Retourne 400 si IllegalArgumentException")
    void createUser_IllegalArgument() throws Exception {
        when(userService.createUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("Le nom d'utilisateur 'admin' n'est pas autorisé"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("admin")));

        verify(userService, times(1)).createUser(any(User.class));
    }

    // ========== Tests pour PUT /api/users/{id} ==========

    @Test
    @DisplayName("PUT /api/users/{id} - Met à jour utilisateur avec succès")
    void updateUser_Success() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPassword("newpassword");

        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username", is("updateduser")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Retourne 404 si utilisateur non trouvé")
    void updateUser_NotFound() throws Exception {
        when(userService.updateUser(anyLong(), any(User.class)))
                .thenThrow(new IllegalArgumentException("Utilisateur non trouvé avec l'ID: 1"));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Utilisateur non trouvé")));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Retourne 400 si validation échoue")
    void updateUser_ValidationFails() throws Exception {
        User invalidUser = new User();
        // Username vide devrait échouer la validation

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(anyLong(), any(User.class));
    }

    // ========== Tests pour DELETE /api/users/{id} ==========

    @Test
    @DisplayName("DELETE /api/users/{id} - Supprime utilisateur avec succès")
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Retourne 404 si utilisateur non trouvé")
    void deleteUser_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Utilisateur non trouvé avec l'ID: 1"))
                .when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Utilisateur non trouvé")));

        verify(userService, times(1)).deleteUser(1L);
    }
}
