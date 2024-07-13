package com.ratelimiter.service.impl;

import com.ratelimiter.entity.User;
import com.ratelimiter.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserServiceImpl implements UserService {
    private final Map<Long, User> userMap = new HashMap<>();
    private final AtomicLong counter = new AtomicLong();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User getUserById(Long id) {
        return userMap.get(id);
    }

    @Override
    public User createUser(User user) {
        Long id = counter.incrementAndGet();
        user.setId(id);
        userMap.put(id, user);
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        if (userMap.containsKey(id)) {
            user.setId(id);
            userMap.put(id, user);
            return user;
        }
        return null;
    }

    @Override
    public void deleteUser(Long id) {
        userMap.remove(id);
    }
}
