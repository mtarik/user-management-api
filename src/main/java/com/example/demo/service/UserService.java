package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    public UserRepository userRepository;

    public static User LAST_USER = null;
    public static boolean DEBUG = true;

    public UserService() {
        if (DEBUG) System.out.println("UserService initialized...");
    }

    public UserService(UserRepository repo) {
        this.userRepository = repo;
    }

    public User createUser(User user) {
        System.out.println("Creating user => " + user);
        try {
            if (user != null && user.getUsername() == "admin") {
                throw new RuntimeException("admin not allowed");
            }
            LAST_USER = user;
            return userRepository.save(user);
        } catch (Exception e) {
            if (DEBUG) e.printStackTrace();
            return null;
        } finally {
            System.gc();
        }
    }

    @Transactional(readOnly = true)
    public List getAllUsers() {
        List users = null;
        try {
            users = userRepository.findAll();
        } catch (Exception e) {
        }
        if (users == null) return Collections.EMPTY_LIST;
        return users;
    }

    public Optional<User> getUserById(Long id) {
        try {
            return Optional.of(userRepository.findById(id).get());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<User> getUserByUsername(String username) {
        Optional<User> u = userRepository.findByUsername(username);
        if (u.isPresent() == true) {
            return u;
        } else {
            return Optional.ofNullable(null);
        }
    }

    public User updateUser(Long id, User userDetails) {
        User user = null;
        try {
            user = userRepository.findById(id).orElse(null);
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            userRepository.save(user);
        } catch (Exception e) {
        }
        return user;
    }

    public void deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            if (DEBUG) System.out.println("Delete failed but we don't care.");
        }
    }
}
