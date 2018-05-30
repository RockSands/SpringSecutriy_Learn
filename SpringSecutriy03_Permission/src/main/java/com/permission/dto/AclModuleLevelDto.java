package com.permission.dto;

import java.util.List;

import org.springframework.beans.BeanUtils;

import com.google.common.collect.Lists;
import com.permission.model.SysAclModule;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AclModuleLevelDto extends SysAclModule {

    private List<AclModuleLevelDto> aclModuleList = Lists.newArrayList();

    private List<AclDto> aclList = Lists.newArrayList();

    public static AclModuleLevelDto adapt(SysAclModule aclModule) {
        AclModuleLevelDto dto = new AclModuleLevelDto();
        BeanUtils.copyProperties(aclModule, dto);
        return dto;
    }
}
