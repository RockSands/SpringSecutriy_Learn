package com.permission.dto;

import java.util.List;

import org.springframework.beans.BeanUtils;

import com.google.common.collect.Lists;
import com.permission.model.SysDept;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeptLevelDto extends SysDept {

    private List<DeptLevelDto> deptList = Lists.newArrayList();

    public static DeptLevelDto adapt(SysDept dept) {
        DeptLevelDto dto = new DeptLevelDto();
        BeanUtils.copyProperties(dept, dto);
        return dto;
    }
}
