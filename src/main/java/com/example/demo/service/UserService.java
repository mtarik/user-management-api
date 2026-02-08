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
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }

        if ("admin".equals(user.getUsername())) {
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
        if (id == null) {
            throw new IllegalArgumentException("L'ID ne peut pas être null");
        }

        if (userDetails == null) {
            throw new IllegalArgumentException("Les détails de l'utilisateur ne peuvent pas être null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + id));

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID ne peut pas être null");
        }

        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + id);
        }

        userRepository.deleteById(id);
    }
}
