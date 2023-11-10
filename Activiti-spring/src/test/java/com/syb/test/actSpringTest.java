package com.syb.test;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: sun
 * @Date: 2023/11/10/16:17
 * spring 整合测试
 */

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration(locations = {"classpath:activiti-spring.xml"}) //加载配置文件
public class actSpringTest {

    @Autowired
    private RepositoryService repositoryService;

    @Test
    public void testRep(){
        System.out.println(repositoryService);
    }
}
