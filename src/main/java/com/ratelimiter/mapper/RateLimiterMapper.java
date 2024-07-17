package com.ratelimiter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.ratelimiter.dto.RateLimiterResponseDTO;
import com.ratelimiter.entity.UserRequestInfo;

@Mapper(componentModel = "spring")
public interface RateLimiterMapper {
	RateLimiterMapper MAPPER = Mappers.getMapper(RateLimiterMapper.class);
	
	@Mapping(source = "requestCount", target = "numberOfRequest")
	RateLimiterResponseDTO entityToDto(UserRequestInfo entity);
}
