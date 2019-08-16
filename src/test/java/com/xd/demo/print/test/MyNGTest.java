package com.xd.demo.print.test;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.Test;
/*
 * @创建人:huangjianfeng
 * @简要描述:
 * @创建时间: 12/19/2018 10:15 AM
 * @参数: 
 * @返回: 
 */
@Slf4j
public class MyNGTest{

    @Test
    public void testcasel(){
        Assert.assertTrue(false);
        log.info("testcasel");

    }

    @Test
    public void testcase2(){
        Assert.assertTrue(false);
        log.info("testcase2");
    }
}
