package com.lontsi.rubberduckpulsarconnector.config;

import com.lontsi.rubberduckpulsarconnector.service.agents.OrchestrationAgent;
import com.lontsi.rubberduckpulsarconnector.tools.SyntheseChatTool;
import com.lontsi.rubberduckpulsarconnector.tools.WebSearchTool;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OrchestrationAgentConfig {

    @Autowired
    @Qualifier("orchestrationModel")
    private ChatModel orchestrationModel;
    @Autowired
    private WebSearchTool webSearchTool;
    @Autowired
    private SyntheseChatTool syntheseChatTool;

    @Bean
    @Qualifier("standardOrchestration")
    public OrchestrationAgent standardOrchestration() {
        return AiServices.builder(OrchestrationAgent.class)
                .chatModel(orchestrationModel)
                .tools(syntheseChatTool)
                .build();
    }

    @Bean
    @Qualifier("professionalOrchestration")
    public OrchestrationAgent professionalOrchestration() {
        return AiServices.builder(OrchestrationAgent.class)
                .chatModel(orchestrationModel)
                .tools(webSearchTool,syntheseChatTool)
                .build();
    }

    @Bean
    @Qualifier("premiumOrchestration")
    public OrchestrationAgent premiumOrchestration() {
        return AiServices.builder(OrchestrationAgent.class)
                .chatModel(orchestrationModel)
                .tools(webSearchTool,syntheseChatTool)
                .build();

    }
}