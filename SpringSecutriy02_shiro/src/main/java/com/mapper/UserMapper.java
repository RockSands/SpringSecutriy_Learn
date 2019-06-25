package com.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.model.User;

@Mapper
public interface UserMapper {

	User findByUserName(@Param("username") String userName);

	int insert(@Param("user") User user);

	int update(User user);

	int insertRoles(@Param("user") User user);

	int del(@Param("uid") String uid);

	int delUserRole(@Param("uid") String uid);
}
