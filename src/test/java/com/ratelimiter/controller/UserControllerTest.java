package com.ratelimiter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ratelimiter.entity.User;
import com.ratelimiter.service.RateLimiter;
import com.ratelimiter.service.UserService;

@SpringBootTest
public class UserControllerTest {
	@Autowired
	private UserController controller;
	
	@Autowired
	private UserService service;
	
	private User userTest;
		
	@BeforeEach
	public void setUp() {
		userTest = service.createUser(User.builder()
				.name("UserOne")
				.lastname("LastNameUser1")
				.email("user1@web.com")
				.phone("123456")
				.age(18)
				.role("admin,oper")
				.build());
	}
		
	@Test
	public void testCreateUser() {
		User newUser = User.builder()
				.name("NewUser")
				.lastname("LastNameNewUser")
				.email("new-user@web.com")
				.phone("123456")
				.age(18)
				.role("admin")
				.build();
		
		ResponseEntity<User> createdUser = controller.createUser(newUser);
		User userResponse = createdUser.getBody();
		
		assertNotNull(userResponse);
		assertEquals(userResponse.getName(), newUser.getName());
	}
	
	@Test
	public void testGetUserById() {
		ResponseEntity<User> data = controller.getUserById(userTest.getId());
		User responseUser = data.getBody();

		assertNotNull(data);
		assertNotNull(responseUser);
		assertEquals(responseUser.getId(), this.userTest.getId());
	}

	@Test
	public void testUpdateUser() {
		userTest.setName("User Updated");
		userTest.setEmail("new.email@web.com");

		ResponseEntity<User> response = controller.updateUser(this.userTest.getId(), userTest);
		User userResponse = response.getBody();

		assertNotNull(response);
		assertNotNull(userResponse);
		assertEquals(userResponse.getName(), this.userTest.getName());
	}

	@Test
	public void testDeleteUser() {
		ResponseEntity<Void> response = controller.deleteUser(userTest.getId());

		User userDeleted = service.getUserById(userTest.getId());
		assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
		assertNull(userDeleted);
	}
	
	@Test
	public void testExecuteTask() {
		ResponseEntity<String> response = controller.executeTask(userTest.getId());
		String body = response.getBody();
		
		assertNotNull(body);
	}
	
	@Test
	public void testRateLimitInformation() {
		ResponseEntity<RateLimiter.UserRequestInfo> response = controller.getRateLimitInfo(userTest.getId());
		RateLimiter.UserRequestInfo body = response.getBody();
		System.out.println(body);
		
		assertNull(body);
	}
	
	@Test
	public void testRateLimitAllUsers() {
		ResponseEntity<List<User>> response =  null;
		
		// Simulating 5 requests
        for (int i = 0; i < 5; i++) {
            response = controller.getAllUsers();
            
            if(i < 5) {
            	assertNotNull(response);            	
            } else {
            	assertNull(response);
            }
            
        }
	}
	
	@Test
	public void testRateLimitGetUser() {
		ResponseEntity<User> response =  null;
		
		// Simulating 5 requests
        for (int i = 0; i < 5; i++) {
            response = controller.getUserById(userTest.getId());
            
            if(i < 5) {
            	assertNotNull(response);            	
            } else {
            	assertNull(response);
            }
            
        }
	}
	
	@Test
	public void testRateLimitCreateUser() {
		ResponseEntity<User> response =  null;
		User newUser = null;
		
		// Simulating 5 requests
        for (int i = 0; i < 5; i++) {
        	newUser = User.builder()
    				.name("NewUser" + i)
    				.lastname("LastNameNewUser" + i)
    				.email("new-user" + i + "@web.com")
    				.phone("123456")
    				.age(18)
    				.role("admin")
    				.build();
        	
            response = controller.createUser(newUser);
            
            if(i < 5) {
            	assertNotNull(response);            	
            } else {
            	assertNull(response);
            }
            
        }
	}
}
