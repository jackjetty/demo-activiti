package com.xd.demo.print.bean;

import lombok.Data;

@Data
public class ResultBean{

    private Integer code;
    private String message;
    public ResultBean(Integer code,String message){
        this.code=code;
        this.message=message;
    }
}