package com.ratelimiter.controller;

import com.ratelimiter.entity.User;
import com.ratelimiter.service.RateLimiter;
import com.ratelimiter.service.UserService;
import com.ratelimiter.service.impl.RateLimiterImpl;
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

    private final RateLimiter rateLimiter = new RateLimiterImpl(5, 10, 60); // 5 requests for regular, 10 for premium, per minute

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        // No rate limiting for fetching all users
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null && rateLimiter.isAllowed(id, user.getRole())) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return updatedUser != null ? new ResponseEntity<>(updatedUser, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/execute-task")
    public ResponseEntity<String> executeTask(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null && rateLimiter.isAllowed(id, user.getRole())) {
            return new ResponseEntity<>("Executed", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
    }

    @PostMapping("/admin/rate-limit-information")
    public ResponseEntity<RateLimiter.UserRequestInfo> getRateLimitInfo(@RequestBody Long userId) {
        RateLimiter.UserRequestInfo info = rateLimiter.getUserRequestInfo(userId);
        return info != null ? new ResponseEntity<>(info, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
