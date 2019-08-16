package com.xd.demo.print.bean;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class ActivitiHelper {
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

    public String getCurrentTaskActivityId(Task task) {
        String excId = task.getExecutionId();
        ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(excId).singleResult();
        String activitiId = execution.getActivityId();
        return activitiId;
    }

    public String getLastTaskActivityId(Task task) {
        String definitionId = task.getProcessDefinitionId();
        String processInstanceId = task.getProcessInstanceId();
        //ProcessDefinition processDefinition=repositoryService.getProcessDefinition(definitionId);

        // 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
      /*  List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(pProcessInstanceId).orderByHistoricActivityInstanceId().asc().list();*/
        // ProcessDefinitionEntity pd = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(task.getProcessDefinitionId());
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
        String currentTaskActivityId = this.getCurrentTaskActivityId(task);
        List<Process> processes = bpmnModel.getProcesses();
        Iterator<FlowElement> iterator;
        boolean isExist = false;
        String lastTaskActivityId = StringUtils.EMPTY;
        FlowNode currentNode;
        for (Process process : processes) {
            currentNode = (FlowNode) process.getFlowElement(currentTaskActivityId);
            if (currentNode == null) {
                continue;
            }
            List<SequenceFlow> flows = currentNode.getIncomingFlows();
            if (flows.size() > 1) {
                return getLastTaskActivityIdByHistory(processInstanceId, currentNode);
            }
            iterator = process.getFlowElements().iterator();
            lastTaskActivityId = StringUtils.EMPTY;
            while (iterator.hasNext()) {
                FlowElement element = iterator.next();
                if (element instanceof UserTask) {
                    if (element.getId().equals(currentTaskActivityId)) {
                        isExist = true;
                        break;
                    }
                    lastTaskActivityId = element.getId();
                }
            }
            if (isExist)
                return lastTaskActivityId;

        }
        return StringUtils.EMPTY;
    }

    public String getLastTaskActivityIdByHistory(String processInstanceId, FlowNode targetNode) {
        List<SequenceFlow> flows = targetNode.getIncomingFlows();
        Set<String> sourceFlowElementIds = Sets.newHashSet();
        for (SequenceFlow sequenceFlow : flows) {
            sourceFlowElementIds.add(sequenceFlow.getSourceFlowElement().getId());
        }
        List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().desc().list();
        String lastActivityId = StringUtils.EMPTY;
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
            lastActivityId = historicActivityInstance.getActivityId();

            if (sourceFlowElementIds.contains(lastActivityId)) {

                return lastActivityId;
            }

        }
        return lastActivityId;

    }

    public String getNextTaskActivityId(Task task) {
        String definitionId = task.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
        String currentTaskActivityId = this.getCurrentTaskActivityId(task);
        //bpmnModel.getMainProcess()
        List<Process> processes = bpmnModel.getProcesses();
        Iterator<FlowElement> iterator;
        boolean isExist = false;
        String nextTaskActivityId = StringUtils.EMPTY;
        FlowNode currentNode;
        for (Process process : processes) {
            currentNode = (FlowNode) process.getFlowElement(currentTaskActivityId);
            if (currentNode == null) {
                continue;
            }
            iterator = process.getFlowElements().iterator();
            while (iterator.hasNext()) {
                FlowElement element = iterator.next();
                if (element instanceof UserTask) {
                    if (isExist) {
                        nextTaskActivityId = element.getId();
                        break;
                    }
                    if (element.getId().equals(currentTaskActivityId)) {
                        isExist = true;
                    }
                }
            }
            if (isExist)
                return nextTaskActivityId;

        }

        return StringUtils.EMPTY;
    }

    public String getFirstTaskActivityId(Task task) {
        String definitionId = task.getProcessDefinitionId();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
        String currentTaskActivityId = this.getCurrentTaskActivityId(task);
        //bpmnModel.getMainProcess()
        List<Process> processes = bpmnModel.getProcesses();
        Iterator<FlowElement> iterator;
        boolean isExist = false;
        String firstTaskActivityId = StringUtils.EMPTY;
        FlowNode currentNode;
        for (Process process : processes) {
            currentNode = (FlowNode) process.getFlowElement(currentTaskActivityId);
            if (currentNode == null) {
                continue;
            }
            boolean isFirst = true;
            iterator = process.getFlowElements().iterator();
            while (iterator.hasNext()) {
                FlowElement element = iterator.next();
                if (element instanceof UserTask) {
                    if (isFirst) {
                        firstTaskActivityId = element.getId();
                    }
                    isFirst = false;
                    if (element.getId().equals(currentTaskActivityId)) {
                        isExist = true;
                    }
                }
            }
            if (isExist)
                return firstTaskActivityId;

        }
        return StringUtils.EMPTY;
    }


    public boolean isMainProcess(Task task) {
        if (StringUtils.isEmpty(task.getParentTaskId())) {
            return false;
        }
        return true;
    }


}
