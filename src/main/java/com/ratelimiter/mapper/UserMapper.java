package com.ratelimiter.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ratelimiter.dto.UserDTO;
import com.ratelimiter.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
	UserMapper MAPPER = Mappers.getMapper(UserMapper.class);
	
	UserDTO entityToDto(User entity);
	
	User dtoToEntity(UserDTO dto);
	
	List<UserDTO> toListDTO(List<User> entities);
}
