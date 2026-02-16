package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ❌ Version avec mauvaises pratiques
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    // ❌ Mauvaise pratique : logger public et non final
    public static org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(UserController.class);

    // ❌ Injection par champ
    @Autowired
    private UserService userService;

    // ❌ Retour direct sans ResponseEntity
    @RequestMapping(method = RequestMethod.GET)
    public List<User> getAllUsers() {
        logger.info("getAllUsers appelé");
        return userService.getAllUsers();
    }

    // ❌ Mauvaise gestion d'erreur + toujours 200
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User getUserById(@PathVariable Long id) {
        try {
            logger.info("Recherche user id = " + id);
            return userService.getUserById(id).get(); // ❌ .get() sans vérification
        } catch (Exception e) {
            logger.error("Erreur : " + e.getMessage());
            return null; // ❌ Retour null
        }
    }

    // ❌ Pas de validation, log de l'objet complet (données sensibles)
    @RequestMapping(method = RequestMethod.POST)
    public User createUser(@RequestBody User user) {
        logger.info("Création user : " + user); // ❌ log objet complet
        try {
            return userService.createUser(user);
        } catch (Exception e) {
            logger.error("Erreur création", e);
            return null;
        }
    }

    // ❌ Mélange logique métier dans le contrôleur
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<User> searchUsers(@RequestParam String name) {
        logger.info("Recherche avec filtre manuel");
        List<User> users = userService.getAllUsers();

        // ❌ Filtrage manuel au lieu d'appeler le service approprié
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(name))
                .toList();
    }

    // ❌ Mauvaise gestion HTTP (toujours 200)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return "Utilisateur supprimé"; // ❌ Texte brut
        } catch (Exception e) {
            return "Erreur suppression"; // ❌ Pas de code HTTP adapté
        }
    }

    // ❌ Endpoint inutilement verbeux
    @RequestMapping(value = "/duplicates", method = RequestMethod.GET)
    public Object findDuplicateUsers() {
        logger.info("Recherche doublons");
        try {
            return userService.findDuplicateUsers();
        } catch (Exception e) {
            return "Erreur interne";
        }
    }
}
