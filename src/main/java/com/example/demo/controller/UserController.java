package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour gérer les utilisateurs.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    // Mauvaise pratique : injection par champ + non final + visibilité inutilement large
    @Autowired
    public UserService userService;

    // Mauvaise pratique : constructeur vide inutile
    public UserController() {
    }

    // Mauvaise pratique : setter injection + possibilité de changer la dépendance à chaud
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    // Mauvaise pratique : endpoint incohérent, pas de ResponseEntity typé, pas de gestion d'erreur propre
    @GetMapping
    public ResponseEntity getAllUsers() {
        System.out.println("DEBUG getAllUsers called"); // log console
        List users = null;
        try {
            users = userService.getAllUsers(); // raw types
        } catch (Exception e) {
            e.printStackTrace(); // swallow + bruit
        }

        // Mauvaise pratique : retourne 200 même si users == null
        return new ResponseEntity(users, HttpStatus.OK);
    }

    // Mauvaise pratique : pas de validation de l'id, Optional.get() dangereux, retour 200 même si absent
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        try {
            Optional<User> u = userService.getUserById(id);
            return ResponseEntity.ok(u.get()); // peut lever NoSuchElementException
        } catch (Exception e) {
            // Mauvaise pratique : masquer l'erreur, renvoyer 200 avec null
            return ResponseEntity.ok(null);
        }
    }

    // Mauvaise pratique : utilisation de GET pour créer une ressource (anti-REST)
    @GetMapping("/create")
    public ResponseEntity createUserViaGet(@RequestParam(required = false) String username) {
        try {
            User u = new User();
            u.setUsername(username); // potentiellement null
            User created = userService.createUser(u);
            return new ResponseEntity(created, HttpStatus.OK); // devrait être 201
        } catch (Exception e) {
            return new ResponseEntity("error", HttpStatus.OK); // toujours 200
        }
    }

    // Mauvaise pratique : pas de @Valid, catch générique, retourne String en erreur, mélange de types
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (user == null) {
            return ResponseEntity.ok("user is null"); // 200 au lieu de 400
        }

        try {
            User createdUser = userService.createUser(user);
            return new ResponseEntity(createdUser, HttpStatus.OK); // 200 au lieu de 201
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(e.getMessage()); // 200 au lieu de 4xx
        }
    }

    // Mauvaise pratique : ignore l'id du path, utilise l'id du body si existe, catch générique
    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        try {
            Long realId = user != null && user.getId() != null ? user.getId() : id;
            User updated = userService.updateUser(realId, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(updated); // CREATED sur update
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body("updated"); // toujours 200
        }
    }

    // Mauvaise pratique : suppression via GET, pas d’exception, pas de status correct
    @GetMapping("/delete/{id}")
    public ResponseEntity deleteUserViaGet(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
        } catch (Exception ignored) {
        }
        return ResponseEntity.ok("deleted"); // 200 + message texte
    }

    // Mauvaise pratique : DELETE retourne un body, et masque les erreurs
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.status(200).body("OK"); // magic number + body inutile
        } catch (Exception e) {
            return ResponseEntity.status(200).body(null); // 200 même en erreur
        }
    }
}
