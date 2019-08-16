package com.xd.demo.print.test;

import com.xd.demo.print.bean.ActivitiHelper;
import com.xd.demo.print.cmd.RollbackFirstTaskCmd;
import com.xd.demo.print.cmd.RollbackLastTaskCmd;
import com.xd.demo.print.cmd.RollbackTaskCmd;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.xd.demo.print.Application;

import javax.annotation.Resource;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class LeaveTest {

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


    @Resource
    private HistoryService historyService;

    @Autowired
    private ActivitiHelper activitiHelper;



    @Test
    public void apply(){
         //开始流程
        //112501
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave");
        log.info("流程创建成功，当前流程实例ID：{}",processInstance.getProcessInstanceId() );

        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();

        //log.info("流程创建成功，当前流程实例ID：{}", task.getId() );
        taskService.claim(task.getId(), "王小二");
        Map<String, Object> vars = new HashMap<>(4);
        vars.put("applyUser",  "王小二");
        vars.put("days", 4);
        vars.put("reason", "我要请假");
        vars.put("manager","刘邓");
        taskService.complete(task.getId(), vars);
        //
    }



    @Test
    public void approve(){
        Task task = taskService.createTaskQuery().processInstanceId("112501").singleResult();
        String userId = "刘邓";
        Map<String, Object> vars = new HashMap<>();
        vars.put("result", "通过");
        vars.put("finance", "小钱");
        vars.put("auditTime", new Date());
        taskService.claim(task.getId(), userId);

        taskService.complete(task.getId(), vars);
    }

    @Test
    public void reject4(){

        Task task = taskService.createTaskQuery().processInstanceId("112501").singleResult();
        String taskId=task.getId();
        //String targetActivityId=activitiHelper.getFirstTaskActivityId(task);
        String targetActivityId="_4";
        RollbackTaskCmd rollbackTaskCmd=new RollbackTaskCmd(taskId,targetActivityId);
        managementService.executeCommand(rollbackTaskCmd);

    }

    @Test
    public void reject3(){
        Task task = taskService.createTaskQuery().processInstanceId("112501").singleResult();

        ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId("112501").singleResult();
        String activitiId = execution.getActivityId();
        log.info("current:{}",activitiId);
        /*String taskId=task.getId();

        RollbackTaskCmd2 rollbackTaskCmd=new RollbackTaskCmd2(taskId,historyService);
        managementService.executeCommand(rollbackTaskCmd);*/



    }

    @Test
    public void checckIn(){

    }

    @Test
    public void reject2(){

        Task task = taskService.createTaskQuery().processInstanceId("112501").singleResult();
        String taskId=task.getId();


        List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId("112501").orderByHistoricActivityInstanceStartTime().asc().list();
        //orderby.asc()
        int index = 0;
        // 已执行的节点ID集合
        List<String> executedActivityIdList = new ArrayList<String>();
        //logger.info("获取已经执行的节点ID");
        for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
            executedActivityIdList.add(activityInstance.getActivityId());

            log.info("第[" + index + "]个已执行节点=" + activityInstance.getActivityId() + " : " +activityInstance.getActivityName());
            index++;
        }
//leave:1:55009
        //String processDefinitionId=taskService.createTaskQuery().processInstanceId("112501").singleResult().getProcessDefinitionId();

         RollbackLastTaskCmd cmd = new RollbackLastTaskCmd(taskId,historicActivityInstanceList.get(1));

         //managementService.executeCommand(cmd);
        /*FlowNode activityImpl = (FlowNode)process.getFlowElement(historicActivityInstanceList.get(11).getActivityId());
        List<SequenceFlow> pvmTransitions = activityImpl.getOutgoingFlows()  ; // 取出节点的所有出去的线
        log.info("验证：{}",activityImpl==null);*/

    }

    @Test
    public void reject(){
        Task task = taskService.createTaskQuery().processInstanceId("112501").singleResult();
        String taskId=task.getId();
        Map<String, Object> variables;
        HistoricTaskInstance currTask = historyService
                .createHistoricTaskInstanceQuery().taskId(taskId)
                .singleResult();
        ProcessInstance instance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(currTask.getProcessInstanceId())
                .singleResult();


        String taskinfoTaskId=getFirstTaskId();
        RollbackFirstTaskCmd cmd = new RollbackFirstTaskCmd(taskId);
        cmd.deletereason="ok";
        managementService.executeCommand(cmd);

    }


    private  String getFirstTaskId(){
        List<HistoricActivityInstance> allTaskInfos = historyService.createHistoricActivityInstanceQuery()
                 .processInstanceId("112501")
                 .list();
        for(HistoricActivityInstance aHistoricActivityInstance:allTaskInfos){
            log.info("taskId:{}",aHistoricActivityInstance.getTaskId());
        }
        return  allTaskInfos.get(3).getId();
    }

    @Test
    public void processInfo(){

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId("145001").singleResult();
        log.info("exist:{}",processInstance==null);

        Task task = taskService.createTaskQuery().processInstanceId("145001").singleResult();
        System.out.println("id="+task.getId());
        System.out.println("name="+task.getName());
        System.out.println("assinee="+task.getAssignee());
        System.out.println("createTime="+task.getCreateTime());
        System.out.println("executionId="+task.getExecutionId());
        Map<String,Object> variables=taskService.getVariables(task.getId());
        System.out.println("var="+variables.toString());
        //taskService.claim(task.getId(), "王小二");

        //taskService.complete(task.getId() );
    }

    @Test
    public void findMyTaskList(){
        String userId = "王小二";
        userId="刘邓";
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
            System.out.println("var="+variables.toString());
        }
    }

}