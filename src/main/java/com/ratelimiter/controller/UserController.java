package com.ratelimiter.controller;

import com.ratelimiter.entity.User;
import com.ratelimiter.entity.UserRequestInfo;
import com.ratelimiter.service.RateLimiter;
import com.ratelimiter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RateLimiter rateLimiter;

    /**
     * Obtener todos los usuarios.
     * No está protegido por el rate limit.
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    /**
     * Obtener un usuario por ID.
     * No está protegido por el rate limit.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return user != null ? new ResponseEntity<>(user, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Crear un nuevo usuario.
     * No está protegido por el rate limit.
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Actualizar un usuario existente.
     * No está protegido por el rate limit.
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return updatedUser != null ? new ResponseEntity<>(updatedUser, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Eliminar un usuario.
     * No está protegido por el rate limit.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Ejecutar una tarea para un usuario específico.
     * Este endpoint está protegido por el rate limit.
     */
    @GetMapping("/{id}/execute-task")
    public ResponseEntity<String> executeTask(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null && rateLimiter.isAllowed(id, user.getRole())) {
            return new ResponseEntity<>("Executed", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
    }

    /**
     * Obtener información de rate limit para un usuario específico.
     */
    @PostMapping("/admin/rate-limit-information")
    public ResponseEntity<UserRequestInfo> getRateLimitInfo(@RequestBody Long userId) {
        UserRequestInfo info = rateLimiter.getUserRequestInfo(userId);
        return info != null ? new ResponseEntity<>(info, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
