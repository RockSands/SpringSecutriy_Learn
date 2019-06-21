package com.mapper;

import java.util.Set;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.model.Permission;
import com.model.Role;

@Mapper
public interface PermissionMapper {
    Set<Permission> findPermissionsByRoleId(@Param("roles") Set<Role> roles);
}