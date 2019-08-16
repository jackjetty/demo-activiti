package com.xd.demo.print.handler;

import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.UserTaskParseHandler;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Slf4j
public class ExtensionUserTaskParseHandler extends UserTaskParseHandler {

    @Override
    protected void executeParse(BpmnParse bpmnParse, UserTask userTask) {
        //调用上层的解析
        super.executeParse(bpmnParse, userTask);
        //log.info("task id:{}",userTask.getId());

        FlowElement flowElement= bpmnParse.getCurrentProcess().getFlowElement( userTask.getId());
        Map<String, List<ExtensionElement>> extMaps= userTask.getExtensionElements();
        List<ExtensionElement> extensionElements;
        if(!CollectionUtils.isEmpty(extMaps)){
            extensionElements=extMaps.get("operations");
            for (ExtensionElement extensionElement : extensionElements){
                log.info("text:{}",extensionElement.getElementText());
                if (extensionElement != null && !extensionElement.getAttributes().isEmpty()) {
                    for (Map.Entry<String, List<ExtensionAttribute>> entry : extensionElement.getAttributes().entrySet()) {
                        System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                    }



                    //for (ExtensionAttribute attributeElement : extensionElement.getAttributes().values()) {
                       // userTaskOperation.addProperty(attributeElement.getName(), attributeElement.getValue());
                    //}
                }
            }
           log.info(extMaps.keySet().toString());
        }

        //

        //ActivityImpl activity = bpmnParse.getCurrentScope().findActivity(userTask.getId());
        //Map<String, ExtensionOperation> operationMap = parseUserTaskOperations(bpmnParse, userTask);

        //将扩展属性设置给activity
        //activity.setProperty(ExtensionBpmnConstants.PROPERTY_OPERATIONS_DEFINITION, operationMap);
    }
/*
    public Map<String, ExtensionOperation> parseUserTaskOperations(BpmnParse bpmnParse, UserTask userTask) {
        Map<String, ExtensionOperation> operationMap = new HashMap<String, ExtensionOperation>();
        //获取扩展属性标签元素
        ExtensionElement operationsElement = userTask.getExtensionElements()
                .get(ExtensionBpmnConstants.EXTENSION_ELEMENT_OPERATIONS);

        if (operationsElement != null) {
            for (ExtensionElement operationElement : operationsElement.getChildElements().values()) {
                ExtensionOperation userTaskOperation = new ExtensionOperation(operationElement.getName());

                if (operationElement != null && !operationElement.getAttributes().isEmpty()) {
                    for (ExtensionAttribute attributeElement : operationElement.getAttributes().values()) {
                        userTaskOperation.addProperty(attributeElement.getName(), attributeElement.getValue());
                    }
                }
                operationMap.put(operationElement.getName(), userTaskOperation);
            }
        }

        return operationMap;
    }*/
}