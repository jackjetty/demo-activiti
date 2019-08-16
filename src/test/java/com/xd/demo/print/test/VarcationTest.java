package com.xd.demo.print.test;

import com.xd.demo.print.bean.ActivitiHelper;
import com.xd.demo.print.cmd.RollbackFirstTaskCmd;
import com.xd.demo.print.cmd.RollbackLastTaskCmd;
import com.xd.demo.print.cmd.RollbackTaskCmd;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
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
public class VarcationTest {
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
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("vacationProcess");
        log.info("流程创建成功，当前流程实例ID：{}",processInstance.getProcessInstanceId() );
        //查询当前的任务
        //52508
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();

        taskService.claim(task.getId(), "王小二");
        Map<String, Object> vars = new HashMap<>(4);
        vars.put("applyUser",  "王小二");
        vars.put("days", 3);
        vars.put("reason", "我要请假");
        taskService.complete(task.getId(), vars);
    }



    @Test
    public void approve(){
        Task task = taskService.createTaskQuery().processInstanceId("132501").singleResult();
        String userId = "李四";
        Map<String, Object> vars = new HashMap<>();
        vars.put("result", "通过");
        vars.put("finance", "大钱");
        vars.put("auditTime", new Date());
        taskService.claim(task.getId(), userId);

        taskService.complete(task.getId(), vars);
    }

    @Test
    public void reject() {
        Task task = taskService.createTaskQuery().processInstanceId("132501").singleResult();
        String taskId = task.getId();
        String lastActivityId = activitiHelper.getLastTaskActivityId(task);
        log.info("lastActivityId:{}",lastActivityId);
    }


    @Test
    public void reject4(){
        Task task = taskService.createTaskQuery().processInstanceId("132501").singleResult();
        String taskId=task.getId();
        String currentActivityId=activitiHelper.getCurrentTaskActivityId(task);
        log.info("currentActivityId :{}",currentActivityId);
        String definitionId=task.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
        Process process=bpmnModel.getMainProcess();
        Iterator<FlowElement> iterator;
        boolean isExist=false;
        String lastTaskActivityId= StringUtils.EMPTY;
        iterator = process.getFlowElements().iterator();
        lastTaskActivityId=StringUtils.EMPTY;
        while(iterator.hasNext()){
            FlowElement element = iterator.next();
            log.info("id:{}",element.getId());
            if(element instanceof UserTask){

            }
        }

        FlowNode targetNode = (FlowNode)process.getFlowElement("_14");
        List<SequenceFlow> flows = targetNode.getIncomingFlows();
        for(SequenceFlow sequenceFlow:flows){
            log.info("size:{}",sequenceFlow.getSourceFlowElement().getId());
        }
        List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId("132501").orderByHistoricActivityInstanceStartTime().desc().list();
        for(HistoricActivityInstance historicActivityInstance:historicActivityInstanceList){
            if(historicActivityInstance.getActivityId().equals("_5")||historicActivityInstance.getActivityId().equals("_6")){
                log.info("activity id:{}",historicActivityInstance.getActivityId());
                return;
            }

        }


        /* String targetActivityId=activitiHelper.getLastTaskActivityId(task);
        log.info("targetActivityId :{}",targetActivityId);
         RollbackTaskCmd rollbackTaskCmd=new RollbackTaskCmd(taskId,"_7");
         managementService.executeCommand(rollbackTaskCmd);*/

    }

    @Test
    public void processInfo(){

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId("132501").singleResult();


        Task task = taskService.createTaskQuery().processInstanceId("132501").singleResult();
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

}