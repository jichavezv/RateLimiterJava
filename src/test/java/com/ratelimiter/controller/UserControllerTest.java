package com.ratelimiter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ratelimiter.dto.UserDTO;
import com.ratelimiter.entity.User;
import com.ratelimiter.entity.UserRequestInfo;
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
		ResponseEntity<UserRequestInfo> response = controller.getRateLimitInfo(userTest.getId());
		UserRequestInfo body = response.getBody();
		System.out.println(body);
		
		assertNull(body);
	}
}
