package com.service;

import com.model.User;

public interface UserService {

    User findByUsername(String username);
}
