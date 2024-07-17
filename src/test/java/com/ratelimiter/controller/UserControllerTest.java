package com.ratelimiter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.ratelimiter.dto.RateLimiterRequestDTO;
import com.ratelimiter.dto.RateLimiterResponseDTO;
import com.ratelimiter.dto.UserDTO;
import com.ratelimiter.entity.User;
import com.ratelimiter.mapper.UserMapper;
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
		
		ResponseEntity<UserDTO> createdUser = controller.createUser(UserMapper.MAPPER.entityToDto(newUser));
		UserDTO userResponse = createdUser.getBody();
		
		assertNotNull(userResponse);
		assertEquals(userResponse.getName(), newUser.getName());
	}
	
	@Test
	public void testGetUserById() {
		ResponseEntity<UserDTO> data = controller.getUserById(userTest.getId());
		UserDTO responseUser = data.getBody();

		assertNotNull(data);
		assertNotNull(responseUser);
		assertEquals(responseUser.getId(), this.userTest.getId());
	}

	@Test
	public void testUpdateUser() {
		userTest.setName("User Updated");
		userTest.setEmail("new.email@web.com");

		ResponseEntity<UserDTO> response = controller.updateUser(this.userTest.getId(), UserMapper.MAPPER.entityToDto(userTest));
		UserDTO userResponse = response.getBody();

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
		ResponseEntity<String> response = null;
		
		// Simulating 5 requests
        for (int i = 0; i < 5; i++) {
        	response = controller.executeTask(userTest.getId());
            
            if(i < 5) {
            	assertNotNull(response);            	
            } else {
            	assertNull(response);
            }
            
        }
		String body = response.getBody();
		
		assertNotNull(body);
	}
	
	@Test
	public void testRateLimitInformation() {
		RateLimiterRequestDTO dto = RateLimiterRequestDTO.builder()
				.userId(userTest.getId())
				.build();
		
		ResponseEntity<RateLimiterResponseDTO> response = controller.getRateLimitInfo(dto);
		RateLimiterResponseDTO body = response.getBody();
		System.out.println(body);
		
		assertNull(body);
	}
	
	@Test
	public void testBlockUser() {
		ResponseEntity<String> response = null;
		HttpStatusCode statusCode = null;
		
		// Simulating 8 request
        for (int i = 1; i < 10; i++) {
        	response = controller.executeTask(userTest.getId());
            statusCode = response.getStatusCode();
            
            System.out.println("i: " + i + " / Status Code: " + statusCode.value());
            
            if(i < 6) {
            	assertEquals(statusCode.value(), 200);            	
            } else if(i > 7) {
            	assertEquals(statusCode.value(), 503);
            } else {
            	assertEquals(statusCode.value(), 429);
            }
            
        }
	}
}
