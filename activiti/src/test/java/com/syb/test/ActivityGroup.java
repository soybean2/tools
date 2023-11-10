package com.syb.test;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: sun
 * @Date: 2023/11/10/14:45
 */
public class ActivityGroup {

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
                .name("请假流程-candidate") //添加部署规则的显示别名  修改act-re-deployment表
                .addClasspathResource("bpmn/evection1.bpmn") //act-re-deployment,act-re-procdef,act-ge-bytearray,act-ge-deployment
                .addClasspathResource("bpmn/evection1.png")  //
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

        String key = "evection-candidate";

        Map map = new HashMap();
        map.put("assignee0", "zhangsan");
        map.put("assignee2", "jerry");
        map.put("assignee3", "make");
        //3.创建流程实例
        ProcessInstance instance = runtimeService
                .startProcessInstanceByKey(key, map);
        //4.输出内容
        System.out.println("流程定义id=" + instance.getProcessDefinitionId());
        System.out.println("流程实例id=" + instance.getId());
        System.out.println("当前活动的id=" + instance.getActivityId());
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
                .processDefinitionKey("evection-candidate") //流程key
                .taskAssignee("zhangsan") //要查询的负责人
                .singleResult();
        if (task != null) {
            taskService.complete(task.getId());
            //4.输出
            System.out.println("完成任务id=" + task.getId());
        }

    }


    /**
     * 1、组任务_查询组任务
     */
    @Test
    public void testGroupTaskList() {
        String key = "evection-candidate";
        String candidateUser = "wangwu";
        //1.获取processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.获取TaskService对象
        TaskService taskService = processEngine.getTaskService();
        //3.根据流程定义的key和任务的负责人查询任务
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey(key)
                .taskCandidateUser(candidateUser).list();//根据候选人查询任务

        for (Task task : list) {
            System.out.println("========================");
            System.out.println("流程实例id=" + task.getProcessInstanceId());
            System.out.println("任务id=" + task.getId());
            System.out.println("任务负责人=" + task.getAssignee());
        }
    }

    /**
     * 2、组任务_拾取组任务，候选人变成了负责人
     */
    @Test
    public void claimTask(){
        //获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取TaskService
        TaskService taskService = processEngine.getTaskService();
        //任务id
        String taskId = "52502";
        //候选人
        String candidateUser = "wangwu";
        //查询任务是否存在
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskCandidateUser(candidateUser)
                .singleResult();
        if (task != null){
            taskService.claim(taskId,candidateUser);//拾取任务，将候选人变成负责人
            System.out.println("taskId="+taskId+"的任务，候选人="+candidateUser+"，拾取任务成功");
        }
    }

    /**
     * 3、组任务_将个人任务回退到组任务，前提是之前一定是组任务
     */
    @Test
    public void testAssigneeToGroup(){
        //获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取TaskService
        TaskService taskService = processEngine.getTaskService();
        //任务id
        String taskId = "52502";
        //候任务负责人
        String candidateUser = "wangwu";
        //查询任务是否存在
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskAssignee(candidateUser)
                .singleResult();
        if (task != null){
            taskService.setAssignee(taskId,null);//将个人任务回退到组任务，前提是之前一定是组任务
            System.out.println("taskId="+taskId+"的任务，候选人="+candidateUser+"，回退任务成功");
        }
    }

    /**
     * 4、组任务_任务交接
     */
    @Test
    public void setAssigneeToCandidateUser(){
        //获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取TaskService
        TaskService taskService = processEngine.getTaskService();
        //任务id
        String taskId = "52502";
        //候任务负责人
        String candidateUser = "zhangsan";
        //查询任务是否存在
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskAssignee(candidateUser)
                .singleResult();
        if (task != null){
            taskService.setAssignee(taskId,"lisi");
            System.out.println("taskId="+taskId+"的任务，候选人="+candidateUser+"，交接任务成功");
        }
    }
}
