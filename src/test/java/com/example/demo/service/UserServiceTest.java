package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour UserService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
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

    // ========== Tests pour createUser() ==========

    @Test
    @DisplayName("createUser - Création réussie d'un utilisateur")
    void createUser_Success() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User created = userService.createUser(testUser);

        assertNotNull(created);
        assertEquals("testuser", created.getUsername());
        assertEquals("test@example.com", created.getEmail());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("createUser - Lève exception si utilisateur null")
    void createUser_NullUser_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(null)
        );

        assertEquals("L'utilisateur ne peut pas être null", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("createUser - Lève exception si username est 'admin'")
    void createUser_AdminUsername_ThrowsException() {
        testUser.setUsername("admin");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(testUser)
        );

        assertEquals("Le nom d'utilisateur 'admin' n'est pas autorisé", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // ========== Tests pour getAllUsers() ==========

    @Test
    @DisplayName("getAllUsers - Retourne la liste de tous les utilisateurs")
    void getAllUsers_ReturnsUserList() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllUsers - Retourne liste vide si aucun utilisateur")
    void getAllUsers_EmptyList() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    // ========== Tests pour getUserById() ==========

    @Test
    @DisplayName("getUserById - Retourne utilisateur si trouvé")
    void getUserById_UserExists_ReturnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getUserById - Retourne Optional vide si utilisateur non trouvé")
    void getUserById_UserNotExists_ReturnsEmpty() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(1L);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getUserById - Retourne Optional vide si ID null")
    void getUserById_NullId_ReturnsEmpty() {
        Optional<User> result = userService.getUserById(null);

        assertFalse(result.isPresent());
        verify(userRepository, never()).findById(any());
    }

    // ========== Tests pour getUserByUsername() ==========

    @Test
    @DisplayName("getUserByUsername - Retourne utilisateur si trouvé")
    void getUserByUsername_UserExists_ReturnsUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("getUserByUsername - Retourne Optional vide si username null")
    void getUserByUsername_NullUsername_ReturnsEmpty() {
        Optional<User> result = userService.getUserByUsername(null);

        assertFalse(result.isPresent());
        verify(userRepository, never()).findByUsername(any());
    }

    // ========== Tests pour updateUser() ==========

    @Test
    @DisplayName("updateUser - Met à jour utilisateur avec succès")
    void updateUser_Success() {
        User updatedDetails = new User();
        updatedDetails.setUsername("updateduser");
        updatedDetails.setEmail("updated@example.com");
        updatedDetails.setPassword("newpassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateUser(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("newpassword", result.getPassword());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("updateUser - Lève exception si ID null")
    void updateUser_NullId_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.updateUser(null, testUser)
        );

        assertEquals("L'ID ne peut pas être null", exception.getMessage());
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("updateUser - Lève exception si userDetails null")
    void updateUser_NullUserDetails_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.updateUser(1L, null)
        );

        assertEquals("Les détails de l'utilisateur ne peuvent pas être null", exception.getMessage());
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("updateUser - Lève exception si utilisateur non trouvé")
    void updateUser_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.updateUser(1L, testUser)
        );

        assertTrue(exception.getMessage().contains("Utilisateur non trouvé"));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    // ========== Tests pour deleteUser() ==========

    @Test
    @DisplayName("deleteUser - Supprime utilisateur avec succès")
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser - Lève exception si ID null")
    void deleteUser_NullId_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.deleteUser(null)
        );

        assertEquals("L'ID ne peut pas être null", exception.getMessage());
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deleteUser - Lève exception si utilisateur non trouvé")
    void deleteUser_UserNotFound_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.deleteUser(1L)
        );

        assertTrue(exception.getMessage().contains("Utilisateur non trouvé"));
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}
