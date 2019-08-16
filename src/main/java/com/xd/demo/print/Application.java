package com.xd.demo.print;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import javax.servlet.Filter;
@Slf4j
@EnableScheduling
@SpringBootApplication
@ServletComponentScan   //servelet注册
@ComponentScan(basePackages = {"com.xd","com.xdbigdata"})
public class Application extends SpringBootServletInitializer{

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main( String[] args ){
         SpringApplication.run(Application.class, args);


    }



}