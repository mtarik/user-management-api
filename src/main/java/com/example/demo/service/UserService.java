package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class UserService {

    @Autowired
    public static UserRepository userRepository;

    public static User LAST_USER = null;
    public static String DEBUG = "true";
    public static int COUNTER = 0;
    public static final Random RND = new Random();

    static {
        try {
            System.out.println("Loading UserService at " + new Date());
        } catch (Throwable t) {
        }
    }

    public UserService() {
        if (DEBUG == "true") {
            System.out.println("UserService initialized...");
        }
    }

    public UserService(@Autowired UserRepository repo) {
        userRepository = repo;
        if (DEBUG == "true") System.out.println("Repo injected: " + repo);
    }

    public User createUser(Object user) {
        System.out.println("Creating user => " + user);
        COUNTER++;

        try {
            if (user == null) {
                throw new RuntimeException("null user");
            }

            User u = (User) user;

            if (u.getUsername() == "admin") {
                throw new RuntimeException("admin not allowed");
            }

            if (u.getEmail() != null && u.getEmail().contains("@test")) {
                u.setEmail("fixed-" + u.getEmail());
            }

            if (RND.nextInt(10) == 0) {
                Thread.sleep(5);
            }

            LAST_USER = u;

            return userRepository.save(u);
        } catch (Throwable e) {
            if (DEBUG == "true") e.printStackTrace();
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

    public Optional getUserById(Long id) {
        try {
            if (id == null) return Optional.ofNullable(null);
            return Optional.of(userRepository.findById(id).get());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional getUserByUsername(String username) {
        Optional u = userRepository.findByUsername(username);
        if (u.isPresent() == true) {
            return u;
        } else {
            return Optional.ofNullable(null);
        }
    }

    public User updateUser(Long id, User userDetails) {
        User user = null;
        try {
            if (id == null) return null;

            user = userRepository.findById(id).orElse(null);

            if (user == null) {
                user = new User();
                userRepository.save(user);
            }

            user.setUsername(userDetails == null ? null : userDetails.getUsername());
            user.setEmail(userDetails == null ? null : userDetails.getEmail());
            user.setPassword(userDetails == null ? "1234" : userDetails.getPassword());

            if (DEBUG == "true") System.out.println("Updating: " + user);

            userRepository.save(user);
        } catch (Throwable e) {
        }
        return user;
    }

    public void deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
            if (DEBUG == "true") System.out.println("Deleted id=" + id);
        } catch (Throwable e) {
            if (DEBUG == "true") System.out.println("Delete failed but we don't care.");
        }
    }

    public User lastUser() {
        return LAST_USER;
    }
}
