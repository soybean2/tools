package com.syb.test;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.junit.Test;

/**
 * @Author: sun
 * @Date: 2023/11/07/17:26
 */
public class TestCreate {

    // 使用代码创建工作流需要的23张表
    // 1.定义流程，使用BPMN规范绘制流程图，保存为xml文件
    // 2.部署流程，把画好的流程图发布到数据库中，生成表的数据
    // 3.启动流程，编写java代码来操作数据库表中的内容，实现我们的业务需求

    @Test
    public void testCreate() {
        // getDefaultProcessEngine()方法会默认从resources下读取名为activiti.cfg.xml的配置文件
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(processEngine);
    }
}
