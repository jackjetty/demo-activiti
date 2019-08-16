package com.xd.demo.print.test;

import com.xd.demo.print.cmd.RollbackFirstTaskCmd;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.xd.demo.print.Application;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class ApplicationTest {

    @Resource
    private RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private IdentityService identityService;

    @Resource
    private ManagementService managementService;


    @Test
    public void testDemo(){
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("processes/demo.bpmn").deploy();
        //获取流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        log.info("流程创建成功，当前流程实例ID：{}",processDefinition.getId() );
        //启动流程定义，返回流程实例
        ProcessInstance pi = runtimeService.startProcessInstanceById(processDefinition.getId());
        String processId = pi.getId();
        log.info("流程创建成功，当前流程实例ID：{}",processId);

        Task task=taskService.createTaskQuery().processInstanceId(processId).singleResult();
        log.info("第一次执行前，任务名称：{}",task.getName());
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
        log.info("第二次执行前，任务名称：{}",task.getName());
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
        log.info("task为null，任务执行完毕：{}",task==null);


    }

    @Test
    public void startVacation(){
//开始流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("vacationProcess");
        log.info("流程创建成功，当前流程实例ID：{}",processInstance.getProcessInstanceId() );
        //查询当前的任务
        //52508
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();
        //声明任务
        //12512
         log.info("流程创建成功，当前流程实例ID：{}", task.getId() );
        taskService.claim(task.getId(), "王小二");
        Map<String, Object> vars = new HashMap<>(4);
        vars.put("applyUser",  "王小二");
        vars.put("days", 4);
        vars.put("reason", "我要请假");
        taskService.complete(task.getId(), vars);
    }

    @Test
    public void passVaction(){
        String userId = "张三";
        Map<String, Object> vars = new HashMap<>();
        vars.put("result", "通过");
        vars.put("auditor", "张三");
        vars.put("auditTime", new Date());
        taskService.claim("35013", userId);
        taskService.complete("35013", vars);
        //


    }

    @Test
    public void rejectVaction(){
        String userId = "张三";
        String taskId="47513";
        RollbackFirstTaskCmd cmd = new RollbackFirstTaskCmd(taskId);
        cmd.deletereason="123";
        managementService.executeCommand(cmd);
    }

    @Test
    public void closeProcess(){
        String taskId="15012";
        Task task=taskService.createTaskQuery() // 创建任务查询
                .taskId(taskId) // 根据任务id查询
                .singleResult();
// //
        runtimeService.deleteProcessInstance(task.getProcessInstanceId(), "xxx原因");
    }



    @Test
    public void findMyTaskList(){
        String userId = "王小二";
         userId="张三";
        //指定个人任务查询
        // 组任务的办理人查询
        //.taskCandidateUser(assignee)
        List<Task> list = taskService
                .createTaskQuery()
                .taskAssignee(userId)
                .orderByTaskCreateTime().desc().list();
        for(Task task:list ){
            System.out.println("id="+task.getId());
            System.out.println("name="+task.getName());
            System.out.println("assinee="+task.getAssignee());
            System.out.println("createTime="+task.getCreateTime());
            System.out.println("executionId="+task.getExecutionId());
            Map<String,Object> variables=taskService.getVariables(task.getId());
            System.out.println("name="+variables.get("reason"));
            System.out.println("name="+task.getProcessVariables().get("reason"));
        }
    }









}