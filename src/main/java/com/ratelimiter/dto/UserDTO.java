package com.ratelimiter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
	private Long id;
    private String name;
    private String lastname;
    private String email;
    private String phone;
    private Integer age;
    private String role;
}
