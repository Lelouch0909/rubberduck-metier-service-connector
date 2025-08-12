package com.lontsi.rubberduckpulsarconnector.config;

import com.lontsi.rubberduckpulsarconnector.service.agents.RouterAgent;
import com.lontsi.rubberduckpulsarconnector.tools.OrchestrationTool;
import com.lontsi.rubberduckpulsarconnector.tools.SaveTool;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterAgentConfig {

    @Autowired
    @Qualifier("routerModel")
    private ChatModel routerModel;
    @Autowired
    private OrchestrationTool orchestrationTool;
    @Autowired
    private SaveTool saveTool;

    @Bean
    public RouterAgent routerAgent() {
        return AiServices.builder(RouterAgent.class)
                .chatModel(routerModel)
                .tools(saveTool, orchestrationTool)
                .build();
    }
}