package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        if (user.getUsername() != null && user.getUsername().equals("admin")) {
            throw new IllegalArgumentException("Le nom d'utilisateur 'admin' n'est pas autorisé");
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur avec l'ID " + id + " non trouvé"));

        if (userDetails.getUsername() != null && !userDetails.getUsername().isEmpty()) {
            user.setUsername(userDetails.getUsername());
        }

        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }

        if (userDetails.getPassword() != null) {
            user.setPassword(userDetails.getPassword());
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Utilisateur avec l'ID " + id + " non trouvé");
        }
        userRepository.deleteById(id);
    }
}
