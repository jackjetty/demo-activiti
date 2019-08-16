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
public class RollbackTaskCmd extends NeedsActiveTaskCmd<Void> {

    private String targetActivityId;

    public RollbackTaskCmd(String taskId, String targetActivityId){
        super(taskId);
        this.targetActivityId=targetActivityId;
    }
    @Override
    protected Void execute(CommandContext commandContext, TaskEntity taskEntity) {
        String processDefinitionId = taskEntity.getProcessDefinitionId();
        String executionId = taskEntity.getExecutionId();
        //找到当前节点
        Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);

        if(StringUtils.isEmpty(targetActivityId)){
            throw new ActivitiException("回退错误，不存在前一步操作");
        }
        FlowNode targetNode = (FlowNode)process.getFlowElement(targetActivityId);
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