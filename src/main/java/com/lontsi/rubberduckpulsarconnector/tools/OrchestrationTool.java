package com.lontsi.rubberduckpulsarconnector.tools;

import com.lontsi.rubberduckpulsarconnector.dto.AssistantTier;
import com.lontsi.rubberduckpulsarconnector.dto.MessageDto;
import com.lontsi.rubberduckpulsarconnector.dto.OrchestrationRequestDto;
import com.lontsi.rubberduckpulsarconnector.service.agents.OrchestrationAgent;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class OrchestrationTool {

    @Autowired
    @Qualifier("orchestrationModel")
    private ChatModel orchestrationModel;

    @Autowired
    private WebSearchTool webSearchTool;

    @Autowired
    private SyntheseChatTool syntheseChatTool;

    @Autowired
    @Qualifier("standardOrchestration")
    private OrchestrationAgent standardorchestrationAgent;

    @Autowired
    @Qualifier("professionalOrchestration")
    private OrchestrationAgent professionalorchestrationAgent;

    @Autowired
    @Qualifier("premiumOrchestration")
    private OrchestrationAgent premiumorchestrationAgent;

    @Tool(name = "orchestrationTool", value = "Orchestrates the conversation by analyzing user requests and determining if external tools are needed.")
    public String handleChat(MessageDto message) {
        log.info("=== ORCHESTRATION START ===");
        log.info("Tier utilisateur: {} ({})", message.tier(), message.tier());
        log.info("Mode assistance: {}", message.mode());
        log.info("Contenu: '{}'", message.content().length() > 100 ?
                message.content().substring(0, 100) + "..." : message.content());

        try {
            OrchestrationAgent selectedAgent = getAgentForTier(message.tier());
            String response = selectedAgent.processChat(new OrchestrationRequestDto(message.content(), message.mode()));
            log.info("=== ORCHESTRATION SUCCESS ===");
            log.warn(response);
            log.info("=== ORCHESTRATION SUCCESS ===");

            return response;
        } catch (Exception e) {
            log.error("=== ORCHESTRATION ERROR ===", e);
            return handleOrchestrationError(message.tier(), e);
        }
    }

    private OrchestrationAgent getAgentForTier(AssistantTier tier) {
        return switch (tier) {
            case STANDARD -> standardorchestrationAgent;
            case PROFESSIONAL -> professionalorchestrationAgent;
            case PREMIUM -> premiumorchestrationAgent;
        };
    }

    private String handleOrchestrationError(AssistantTier tier, Exception e) {
        String errorMessage = switch (tier) {
            case STANDARD -> "Une erreur s'est produite avec votre Assistant Standard. Veuillez réessayer.";
            case PROFESSIONAL ->
                    "Une erreur s'est produite avec votre Assistant Professionnel. Notre équipe a été notifiée.";
            case PREMIUM ->
                    "Une erreur s'est produite avec votre Assistant Premium. Support prioritaire contacté automatiquement.";
        };

        log.error("Erreur orchestration - Tier: {}, Message: {}", tier, e.getMessage());
        return errorMessage;
    }


}