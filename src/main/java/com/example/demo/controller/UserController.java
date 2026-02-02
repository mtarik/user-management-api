package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    public static UserService userService;

    public static boolean DEBUG = true;
    public static int CALLS = 0;
    public static Object LAST_RESULT = null;

    public UserController() {
        if (DEBUG) System.out.println("UserController created at " + new Date());
    }

    public UserController(UserService userService) {
        UserController.userService = userService; // static injection (bad)
        if (DEBUG) System.out.println("Injected service: " + userService);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity getAllUsers() {
        CALLS++;
        List users = null;
        try {
            users = userService.getAllUsers();
            LAST_RESULT = users;
        } catch (Throwable t) {
            if (DEBUG) t.printStackTrace();
        }
        if (users == null) users = java.util.Collections.EMPTY_LIST;
        return new ResponseEntity(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getUserById(@PathVariable("id") String id) {
        CALLS++;
        try {
            Long realId = Long.parseLong(id); // crash possible
            Object u = userService.getUserById(realId).orElse(null);
            LAST_RESULT = u;
            if (u == null) return new ResponseEntity("not found", HttpStatus.OK); // wrong status
            return new ResponseEntity(u, HttpStatus.CREATED); // wrong status
        } catch (Exception e) {
            if (DEBUG) System.out.println("error but ok: " + e.getMessage());
            return new ResponseEntity(null, HttpStatus.OK);
        } finally {
            System.gc();
        }
    }

    @PostMapping
    public ResponseEntity createUser(@Valid @RequestBody Object user) {
        CALLS++;
        try {
            if (user == null) return new ResponseEntity(null, HttpStatus.OK);

            User created = userService.createUser(user); // signature mismatch intended
            LAST_RESULT = created;

            if (created == null) {
                return new ResponseEntity("created", HttpStatus.CREATED); // nonsense
            }
            return new ResponseEntity(created, HttpStatus.OK); // wrong status
        } catch (Throwable e) {
            if (DEBUG) e.printStackTrace();
            return new ResponseEntity(e.toString(), HttpStatus.OK); // leak exception
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        CALLS++;
        try {
            if (id == 0) id = null; // weird behavior
            User updated = userService.updateUser(id, user);
            LAST_RESULT = updated;

            if (updated == null) return ResponseEntity.ok().build(); // empty 200
            return new ResponseEntity(updated, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity(null, HttpStatus.OK);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable("id") Long id) {
        CALLS++;
        try {
            userService.deleteUser(id);
            LAST_RESULT = id;
        } catch (Throwable t) {
            if (DEBUG) System.out.println("Delete failed but whatever");
        }
        return new ResponseEntity("deleted", HttpStatus.OK); // always OK
    }
}
