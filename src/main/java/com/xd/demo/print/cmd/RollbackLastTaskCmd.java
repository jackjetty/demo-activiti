package com.xd.demo.print.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Slf4j
public class RollbackLastTaskCmd extends NeedsActiveTaskCmd<Void> {

    private HistoricActivityInstance activityInstance;
    public RollbackLastTaskCmd(String taskId,HistoricActivityInstance activityInstance){
        super(taskId);
        this.activityInstance=activityInstance;
    }
    @Override
    public Void execute(CommandContext commandContext, TaskEntity taskEntity) {
        String processDefinitionId = taskEntity.getProcessDefinitionId();
        log.info("验证：{}",processDefinitionId);
        String executionId = taskEntity.getExecutionId();

        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        taskEntityManager.deleteTask(taskEntity, "123", false, false);

        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findById(executionId);
        Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
        FlowNode flowNode = (FlowNode)process.getFlowElement( activityInstance.getActivityId());
        SequenceFlow sequenceFlow = flowNode.getOutgoingFlows()
                .get(0);
        FlowNode targetActivity = (FlowNode) sequenceFlow.getTargetFlowElement();

        List<SequenceFlow> flows = targetActivity.getIncomingFlows();
        if (flows == null || flows.isEmpty()) {
            throw new ActivitiException("回退错误，目标节点没有来源连线");
        }
        // 随便选一条连线来执行，时当前执行计划为，从连线流转到目标节点，实现跳转
        executionEntity.setCurrentFlowElement(flows.get(0));
        commandContext.getAgenda().planTakeOutgoingSequenceFlowsOperation(executionEntity, true);

        return null;
    }
}