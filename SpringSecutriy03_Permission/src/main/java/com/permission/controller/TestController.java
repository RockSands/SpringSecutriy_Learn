package com.permission.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.permission.common.ApplicationContextHelper;
import com.permission.common.JsonData;
import com.permission.dao.SysAclModuleMapper;
import com.permission.exception.ParamException;
import com.permission.exception.PermissionException;
import com.permission.model.SysAclModule;
import com.permission.param.TestVo;
import com.permission.util.BeanValidator;
import com.permission.util.JsonMapper;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {

    @RequestMapping("/hello.json")
    @ResponseBody
    public JsonData hello() {
        log.info("hello");
        throw new PermissionException("test exception");
        // return JsonData.success("hello, permission");
    }

    @RequestMapping("/validate.json")
    @ResponseBody
    public JsonData validate(TestVo vo) throws ParamException {
        log.info("validate");
        SysAclModuleMapper moduleMapper = ApplicationContextHelper.popBean(SysAclModuleMapper.class);
        SysAclModule module = moduleMapper.selectByPrimaryKey(1);
        log.info(JsonMapper.obj2String(module));
        BeanValidator.check(vo);
        return JsonData.success("test validate");
    }
}
