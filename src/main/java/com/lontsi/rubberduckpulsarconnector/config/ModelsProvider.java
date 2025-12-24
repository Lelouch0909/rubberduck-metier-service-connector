package com.lontsi.rubberduckpulsarconnector.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.github.GitHubModelsChatModel;
import dev.langchain4j.model.github.GitHubModelsChatModelName;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelsProvider {

    @Value("${model.github.token}")
    private String GITHUB_TOKEN;


    @Bean
    @Qualifier("OrchestrationModel")
    ChatModel orchestrationModel() {
        return GitHubModelsChatModel.builder()
                .gitHubToken(GITHUB_TOKEN)
                .modelName("gpt-4.1")
                .logRequestsAndResponses(true)
                .build();
    }

    @Bean
    @Qualifier("routerModel")
    ChatModel routerModel() {
        return GitHubModelsChatModel.builder()
                .gitHubToken(GITHUB_TOKEN)
                .modelName("gpt-4.1")
                .logRequestsAndResponses(true)
                .build();
    }

    @Bean
    @Qualifier("syntheseModel")
    ChatModel syntheseModel() {
        return GitHubModelsChatModel.builder()
                .gitHubToken(GITHUB_TOKEN)
                .modelName("gpt-4.1")
                .logRequestsAndResponses(true)
                .build();
    }
}
