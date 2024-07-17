package com.ratelimiter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ratelimiter.dto.RateLimiterRequestDTO;
import com.ratelimiter.dto.RateLimiterResponseDTO;
import com.ratelimiter.dto.UserDTO;
import com.ratelimiter.entity.User;
import com.ratelimiter.entity.UserRequestInfo;
import com.ratelimiter.mapper.RateLimiterMapper;
import com.ratelimiter.mapper.UserMapper;
import com.ratelimiter.service.RateLimiter;
import com.ratelimiter.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RateLimiter rateLimiter;

    /**
     * Get All Users
     * @return List of Users
     * @author Juan Ignacio Chavez
     * @since Jul/13/2024
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
    	List<User> list = userService.getAllUsers();
        return new ResponseEntity<>(UserMapper.MAPPER.toListDTO(list), HttpStatus.OK);
    }

    /**
     * Get a User by Id
     * @param id User Id
     * @return User data
     * @author Juan Ignacio Chavez
     * @since Jul/13/2024
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return user != null ? new ResponseEntity<>(UserMapper.MAPPER.entityToDto(user), HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Create a User
     * @param user Object with the User data
     * @return User data created
     * @author Juan Ignacio Chavez
     * @since Jul/13/2024
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
        User createdUser = userService.createUser(UserMapper.MAPPER.dtoToEntity(user));
        return new ResponseEntity<>(UserMapper.MAPPER.entityToDto(createdUser), HttpStatus.CREATED);
    }

    /**
     * Update a User
     * @param id User Id
     * @param user Object with User data to update
     * @return Object User updated
     * @author Juan Ignacio Chavez
     * @since Jul/13/2024
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO user) {
        User updatedUser = userService.updateUser(id, UserMapper.MAPPER.dtoToEntity(user));
        return updatedUser != null ? new ResponseEntity<>(UserMapper.MAPPER.entityToDto(updatedUser), HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Delete a User
     * @param id User Id
     * @return HTTP code 204
     * @author Juan Ignacio Chavez
     * @since Jul/13/2024
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
    /**
     * Execute a task from a User
     * @param id User Id
     * @return A String to indicate if is executed
     * @author Juan Ignacio Chavez
     * @since Jul/13/2024
     */
    @GetMapping("/{id}/execute-task")
    public ResponseEntity<String> executeTask(@PathVariable Long id) {
    	log.info("ID: " + id);
        User user = userService.getUserById(id);
        if (user != null && rateLimiter.isAllowed(id, user.getRole())) {
            return new ResponseEntity<>("Executed", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
    }
    
    /**
     * Get the rate limit information for a user
     * @param userId User Id
     * @return The User information for each request
     * @author Juan Ignacio Chavez
     * @since Jul/13/2024
     */
    @PostMapping("/admin/rate-limit-information")
    public ResponseEntity<RateLimiterResponseDTO> getRateLimitInfo(@RequestBody RateLimiterRequestDTO dto) {
        UserRequestInfo info = rateLimiter.getUserRequestInfo(dto.getUserId());
        return info != null ? new ResponseEntity<>(RateLimiterMapper.MAPPER.entityToDto(info), HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
