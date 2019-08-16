package com.xd.demo.print.config.activiti;

import com.google.common.collect.Lists;
import com.xd.demo.print.handler.ExtensionUserTaskParseHandler;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.impl.history.HistoryLevel;
import org.activiti.engine.parse.BpmnParseHandler;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MyProcessEngineConfigurationConfigurer implements ProcessEngineConfigurationConfigurer {


    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
        processEngineConfiguration.setActivityFontName("宋体");
        processEngineConfiguration.setLabelFontName("宋体");
        processEngineConfiguration.setAnnotationFontName("宋体");
        processEngineConfiguration.setHistory(HistoryLevel.AUDIT.getKey());
        List<BpmnParseHandler>  bpmnParseHandlers= Lists.newArrayList();
        bpmnParseHandlers.add(extensionUserTaskParseHandler());
        processEngineConfiguration.setCustomDefaultBpmnParseHandlers(bpmnParseHandlers);
    }

    @Bean
    public ExtensionUserTaskParseHandler extensionUserTaskParseHandler(){
        return new ExtensionUserTaskParseHandler();
    }

}

