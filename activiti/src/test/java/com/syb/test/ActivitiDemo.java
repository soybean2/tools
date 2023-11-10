package com.syb.test;

import com.syb.demo.pojo.Evection;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * @Author: sun
 * @Date: 2023/11/08/19:10
 */
public class ActivitiDemo {

    /**
     * 流程部署
     */
    @Test
    public void testDeployment() {
        //1.创建ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.获取RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3.使用service进行流程的部署，定义一个流程的名字，把bpmn和png部署到数据库中
        Deployment deploy = repositoryService.createDeployment()
                .name("请假流程") //添加部署规则的显示别名  修改act-re-deployment表
                .addClasspathResource("bpmn/evection.bpmn") //act-re-deployment,act-re-procdef,act-ge-bytearray,act-ge-deployment
                .addClasspathResource("bpmn/evection.png")  //
                .deploy();

        //4.输出部署信息
        System.out.println("流程部署id=" + deploy.getId());
        System.out.println("流程部署名字=" + deploy.getName());

    }

    /**
     * 启动流程实例
     */
    @Test
    public void testStartProcessInstance() {
        //1.获取processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.获取RunTimeService对象
        RuntimeService runtimeService = processEngine.getRuntimeService();

        String key = "myEvection";
        //3.创建流程实例
        ProcessInstance instance = runtimeService
                .startProcessInstanceByKey(key);
        //4.输出内容
        System.out.println("流程定义id=" + instance.getProcessDefinitionId());
        System.out.println("流程实例id=" + instance.getId());
        System.out.println("当前活动的id=" + instance.getActivityId());
    }

    /**
     * 查询当前个人任务
     */
    @Test
    public void testFindPersonalTaskList() {
        //任务负责人
        String assignee = "lisi";
        //1.获取流程引擎ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.获取TaskService对象
        TaskService taskService = processEngine.getTaskService();
        //3.根据流程key和任务负责人 查询任务
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("myEvection") //流程key
                .taskAssignee(assignee) //要查询的负责人
                .list();
        //4.输出
        for (Task task : list) {
            System.out.println("流程实例id=" + task.getProcessInstanceId());
            System.out.println("任务id=" + task.getId());
            System.out.println("任务负责人=" + task.getAssignee());
            System.out.println("任务名称=" + task.getName());
        }
    }


    /**
     * 完成个人任务
     */
    @Test
    public void completTask() {

        //1.获取流程引擎ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.获取TaskService对象
        TaskService taskService = processEngine.getTaskService();
        //3.根据流程key和任务负责人 查询任务id,完成任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("myEvection") //流程key
                .taskAssignee("lisi") //要查询的负责人
                .singleResult();
        if (task != null) {
            taskService.complete(task.getId());
            //4.输出
            System.out.println("完成任务id=" + task.getId());
        }

    }

    /**
     * 使用zip包进行批量的部署
     */
    @Test
    public void deployProcessByZip() {
        //获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //流程部署
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("bpmn/evection.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        //使用压缩包的流进行部署
        Deployment deploy = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println("流程部署id=" + deploy.getId());
        System.out.println("流程部署名字=" + deploy.getName());
        System.out.println("流程部署key=" + deploy.getKey());
    }


    /**
     * 流程定义信息的查询
     */
    @Test
    public void queryProcessDefinition() {
        //1、获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2、获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3、获取ProcessDefinitionQuery对象
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("myEvection")
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
        //输出
        for (ProcessDefinition processDefinition : list) {
            System.out.println("流程定义id=" + processDefinition.getId());
            System.out.println("流程定义名称=" + processDefinition.getName());
            System.out.println("流程定义key=" + processDefinition.getKey());
            System.out.println("流程定义版本=" + processDefinition.getVersion());
            System.out.println("流程部署id=" + processDefinition.getDeploymentId());
        }
    }

    /**
     * 删除流程定义 act_re_deployment
     * 默认情况下，如果使用流程部署对象进行删除，只能删除没有启动的流程，如果流程启动就会抛出异常
     * 可以使用级联删除，此时就会先删除没有启动的流程，最后删除启动的流程，一般情况下不建议使用级联删除
     * act_ge_bytearray
     * act_re_deployment
     * act_re_procdef
     */
    @Test
    public void deleteProcessDefinition() {
        //1、获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2、获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3、删除流程定义，参数代表流程部署id
//        repositoryService.deleteDeployment("12501");
        repositoryService.deleteDeployment("30001", true);//级联删除
    }

    /**
     * 下载资源文件
     * 方案1：使用activiti提供的api来下载资源文件，使用的是默认提供的文件名
     * 方案2：自己写代码从数据库中下载文件，使用任务id来获取流程实例对象，
     * 从act_ge_bytearray表中读取文件，读取jdbc中blob类型、clob类型数据
     * 解决io操作：commons-io.jar工具包中的IOUtils
     */
    @Test
    public void getDeployment() throws Exception {
        //1、获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2、获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3、获取查询对象ProcessDefinition，查询流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("myEvection")
                .singleResult();
        //4、通过流程定义信息，获取部署id
        String deploymentId = processDefinition.getDeploymentId();
        //5、通过repositoryService的方法，实现读取图片信息及bpmn文件信息（输入流）
        //getResourceAsStream()方法的参数说明：第一个参数部署id，第二个参数代表资源名称
        //processDefinition.getDiagramResourceName()获取png图片资源的名称
        //processDefinition.getResourceName()获取bpmn文件的名称
        InputStream pngIs = repositoryService.getResourceAsStream(deploymentId, processDefinition.getDiagramResourceName());
        InputStream bpmnIs = repositoryService.getResourceAsStream(deploymentId, processDefinition.getResourceName());
        // 文件的输出
        File pngFile = new File("D:/evectionflow01.png");
        File bpmnFile = new File("D:/evectionflow01.bpmn");
        //构造OutputStream流
        FileOutputStream pngOutputStream = new FileOutputStream(pngFile);
        FileOutputStream bpmnOutputStream = new FileOutputStream(bpmnFile);
        //输出流
        IOUtils.copy(pngIs, pngOutputStream);
        IOUtils.copy(bpmnIs, bpmnOutputStream);
        //输出到文件中
        pngOutputStream.close();
        bpmnOutputStream.close();
        pngIs.close();
        bpmnIs.close();
    }

    /**
     * 流程历史信息的查看
     */
    @Test
    public void findHistoryInfo() {
        //1、获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2、获取HistoryService
        HistoryService historyService = processEngine.getHistoryService();
        //3、获取actinst表的查询对象
        HistoricActivityInstanceQuery instanceQuery = historyService.createHistoricActivityInstanceQuery();
        //4、设置查询参数
//        instanceQuery.processInstanceId("15001");
        instanceQuery.processDefinitionId("myEvection:1:12504");
        //排序,根据开始时间排序，升序
        instanceQuery.orderByHistoricActivityInstanceStartTime().asc();
        //5、执行查询

        //6、输出
        List<HistoricActivityInstance> list = instanceQuery.list();
        for (HistoricActivityInstance hi : list) {
            System.out.println("活动id：" + hi.getId());
            System.out.println("流程实例id：" + hi.getProcessInstanceId());
            System.out.println("节点id：" + hi.getActivityId());
            System.out.println("活动名称：" + hi.getActivityName());
            System.out.println("办理人：" + hi.getAssignee());
            System.out.println("开始时间：" + hi.getStartTime());
            System.out.println("结束时间：" + hi.getEndTime());
            System.out.println("=======================================");
        }

    }

    /**
     * 添加业务key到activiti的表中
     */
    @Test
    public void addBusinessKey() {
        //1.获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //3.启动流程实例的同时，设置业务key，它本身就是请假单的id
        //第一个参数：流程定义key
        //第二个参数：业务key，就是请假单的id，就是business_key字段
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("myEvection", "1001");
        System.out.println("业务key=" + instance.getBusinessKey());
    }

    /**
     * 全部流程实例的挂起和激活
     * suspend 暂停
     * 1 激活 2 挂起
     */
    @Test
    public void suspendAllProcessInstance() {
        //1.获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3.查询流程定义的对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("myEvection")
                .singleResult();
        //4.得到当前流程定义的实例是否都为暂停状态
        boolean suspended = processDefinition.isSuspended();
        //5.获取流程定义id
        String definitionId = processDefinition.getId();
        //6.如果是暂停，改为激活
        if (suspended) {
            //说明是暂停，可以激活，参数1：流程定义id，参数2：是否激活，参数3：激活时间
            repositoryService.activateProcessDefinitionById(definitionId, true, null);
            System.out.println("流程定义：" + definitionId + "激活");
        } else {
            //说明是激活，可以暂停
            repositoryService.suspendProcessDefinitionById(definitionId, true, null);
            System.out.println("流程定义：" + definitionId + "挂起");
        }
    }

    /**
     * 单个流程实例的挂起和激活
     * suspend 暂停
     * 1 激活 2 挂起
     */
    @Test
    public void suspendSingleProcessInstance() {
        //1.获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //3.查询流程实例对象
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId("32501")
                .singleResult();
        //4.得到当前流程定义的实例是否都为暂停状态 true代表暂停 false代表激活
        boolean suspended = instance.isSuspended();
        //5.获取流程实例id
        String instanceId = instance.getId();
        //6.如果是暂停，改为激活
        if (suspended) {
            //说明是暂停，可以激活，参数1：流程实例id，参数2：是否激活，参数3：激活时间
            runtimeService.activateProcessInstanceById(instanceId);
            System.out.println("流程实例：" + instanceId + "已经激活");
        } else {
            //说明是激活，可以暂停
            runtimeService.suspendProcessInstanceById(instanceId);
            System.out.println("流程实例：" + instanceId + "已经挂起");
        }
    }

    /**
     * 完成个人任务
     */
    @Test
    public void completTask1() {

        //1.获取流程引擎ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.获取TaskService对象
        TaskService taskService = processEngine.getTaskService();
        //3.根据流程key和任务负责人 查询任务id,完成任务
        Task task = taskService.createTaskQuery()
                .processInstanceId("32501")
                .taskAssignee("zhangsan") //要查询的负责人
                .singleResult();
        // 根据id完成任务
        taskService.complete(task.getId());
        //4.输出
        System.out.println("流程任务id=" + task.getId());
        System.out.println("流程实例id=" + task.getProcessInstanceId());
        System.out.println("负责人=" + task.getAssignee());
        System.out.println("任务名称=" + task.getName());

    }

    /**
     * 启动流程时设置变量
     */
    @Test
    public void startProcessInstance2() {
        //1.获取流程引擎ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.获取RunTimeService对象
        RuntimeService runtimeService = processEngine.getRuntimeService();

        String key = "myEvection";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("assignee0", "zhangsan");
        map.put("assignee1", "lisi");
        map.put("assignee2", "wangwu");
        map.put("assignee3", "zhaoliu");
        Evection evection = new Evection();
        evection.setNum(3d);
        map.put("evection", evection);
        //3.创建流程实例
        ProcessInstance instance = runtimeService
                .startProcessInstanceByKey(key, map);
        //4.输出内容
        System.out.println("流程定义id=" + instance.getProcessDefinitionId());
        System.out.println("流程实例id=" + instance.getId());
        System.out.println("当前活动的id=" + instance.getActivityId());
    }

    /**
     * 任务完成时添加变量
     * 启动时不设置变量
     */
    @Test
    public void completTask2() {

        //1.获取流程引擎ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.获取TaskService对象
        TaskService taskService = processEngine.getTaskService();
        //3.根据流程key和任务负责人 查询任务id,完成任务
        Task task = taskService.createTaskQuery()
                .processInstanceId("32501")
                .taskAssignee("zhangsan") //要查询的负责人
                .singleResult();
        // 根据id完成任务
        Map<String, Object> map = new HashMap<String, Object>();
        Evection evection = new Evection();
        evection.setNum(3d);
        map.put("evection", evection);
        //根据id完成任务并添加流程变量
        taskService.complete(task.getId(), map);
        //4.输出
        System.out.println("流程任务id=" + task.getId());
        System.out.println("流程实例id=" + task.getProcessInstanceId());
        System.out.println("负责人=" + task.getAssignee());
        System.out.println("任务名称=" + task.getName());

    }

}
