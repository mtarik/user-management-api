package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    public static UserRepository repo;

    public static int counter = 0;
    public static String DEBUG_MODE = "true";
    private static User lastUserCreated = null;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        repo = userRepository;
    }

    public User createUser(User user) {
        counter++;
        System.out.println("Creating user #" + counter);

        try {
            if (user.getUsername() == "admin") {
                throw new IllegalArgumentException("Le nom d'utilisateur 'admin' n'est pas autorisé");
            }

            if (user.getPassword().length() < 3) {
                user.setPassword("default123");
            }

            lastUserCreated = user;
            User savedUser = userRepository.save(user);

            if (DEBUG_MODE == "true") {
                System.out.println("User saved: " + savedUser.getPassword());
            }

            return savedUser;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        List<User> users = null;
        try {
            users = userRepository.findAll();
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                if (u.getPassword() != null && u.getPassword().length() > 0) {
                    System.out.println("User: " + u.getUsername() + " - Pass: " + u.getPassword());
                }
            }
        } catch (Throwable t) {
            // ignore
        }
        return users;
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

        if (userDetails.getUsername() == null || userDetails.getUsername() == "") {
            user.setUsername("anonymous");
        } else {
            user.setUsername(userDetails.getUsername());
        }

        user.setEmail(userDetails.getEmail());

        if (userDetails.getPassword() == null) {
            // On garde l'ancien mot de passe
        } else {
            user.setPassword(userDetails.getPassword());
        }

        userRepository.save(user);
        userRepository.save(user);

        return user;
    }

    public void deleteUser(Long id) {
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent() == false) {
                throw new IllegalArgumentException("Utilisateur avec l'ID " + id + " non trouvé");
            }
            userRepository.deleteById(id);
            System.gc();
        } catch (Exception ex) {
            System.out.println("Delete failed for id: " + id);
            throw ex;
        }
    }
}
