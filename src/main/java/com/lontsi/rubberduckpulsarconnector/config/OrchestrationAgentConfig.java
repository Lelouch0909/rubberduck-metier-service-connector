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
    List<Object> standardTools = List.of(syntheseChatTool);
    List<Object> professionalTools = List.of(
            syntheseChatTool,
            webSearchTool
    );
    List<Object> premiumTools = List.of(
            syntheseChatTool,
            webSearchTool
    );

    @Bean
    @Qualifier("standardOrchestration")
    public OrchestrationAgent standardOrchestration() {
        return AiServices.builder(OrchestrationAgent.class)
                .chatModel(orchestrationModel)
                .tools(standardTools)
                .build();
    }

    @Bean
    @Qualifier("professionalOrchestration")
    public OrchestrationAgent professionalOrchestration() {
        return AiServices.builder(OrchestrationAgent.class)
                .chatModel(orchestrationModel)
                .tools(professionalTools)
                .build();
    }

    @Bean
    @Qualifier("premiumOrchestration")
    public OrchestrationAgent premiumOrchestration() {
        return AiServices.builder(OrchestrationAgent.class)
                .chatModel(orchestrationModel)
                .tools(premiumTools)
                .build();

    }
}