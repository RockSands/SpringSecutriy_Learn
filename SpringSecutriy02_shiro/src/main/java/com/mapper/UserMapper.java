package com.mapper;

import org.apache.ibatis.annotations.Param;

import com.model.User;

public interface UserMapper {

    User findByUsername(@Param("username") String username);
}
