package com.xd.demo.print.cmd;

import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.history.HistoryManager;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;
import org.thymeleaf.util.StringUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


@Slf4j
public class RollbackTaskCmd2 extends NeedsActiveTaskCmd<Void> {

    private HistoryService historyService;

    public RollbackTaskCmd2(String taskId, HistoryService historyService){
        super(taskId);
        this.historyService=historyService;
    }
    @Override
    protected Void execute(CommandContext commandContext, TaskEntity taskEntity) {
        String processDefinitionId = taskEntity.getProcessDefinitionId();
        String executionId = taskEntity.getExecutionId();
        String processInstanceId=taskEntity.getProcessInstanceId();

        List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
        String currentTaskDefKey="";
        for(HistoricActivityInstance historicActivityInstance:historicActivityInstanceList){
            //log.info("历史节点{},{},{}",historicActivityInstance.getActivityId(),historicActivityInstance.getActivityName(),historicActivityInstance.getActivityType());
            currentTaskDefKey=historicActivityInstance.getActivityId();
        }
        //找到当前节点
        Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
        Iterator<FlowElement> iterator = process.getFlowElements().iterator();
        String targetTaskDefKey="";
        while(iterator.hasNext()){
            FlowElement element = iterator.next();
            //if(element instanceof StartEvent){
            if(element instanceof UserTask){
                if(element.getId().equals(currentTaskDefKey))
                    break;
                targetTaskDefKey=element.getId();
            }
        }
        if(StringUtils.isEmpty(targetTaskDefKey)){
            throw new ActivitiException("回退错误，不存在前一步操作");
        }




        FlowNode targetNode = (FlowNode)process.getFlowElement(targetTaskDefKey);
        List<SequenceFlow> flows = targetNode.getIncomingFlows();
        if(flows==null || flows.size()<1){
            throw new ActivitiException("回退错误，目标节点没有来源连线");
        }
        //删除当前任务
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        taskEntityManager.deleteTask(taskEntity, "回退删除任务", false, false);
        //随便选一条连线来执行，时当前执行计划为，从连线流转到目标节点，实现跳转
        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findById(executionId);
        executionEntity.setCurrentFlowElement(flows.get(0));
        commandContext.getAgenda().planTakeOutgoingSequenceFlowsOperation(executionEntity, true);





        return null;
    }
}