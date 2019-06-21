package com.mapper;

import java.util.Set;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.model.Role;

@Mapper
public interface RoleMapper {
	Set<Role> findRolesByUserId(@Param("uid") Integer uid);
}
