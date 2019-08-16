package com.xd.demo.print.controller;

import com.xd.demo.print.bean.ResultBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.activiti.engine.IdentityService;


@Slf4j

@Controller
public class PrintController{

    @RequestMapping({"/","/index" })
    public String index(HttpServletRequest request, HttpServletResponse response, Model model){


      return "index";

    }
    @GetMapping(value = "/test", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public  ResultBean getAlarmOrderPageInfo(HttpServletRequest request, HttpServletResponse response, Model model ) {

        return  new ResultBean(0,"success");

    }
}