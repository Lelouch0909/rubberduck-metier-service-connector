package com.lontsi.rubberduckpulsarconnector.tools;

import com.lontsi.rubberduckpulsarconnector.dto.EnrichedMessageDto;
import com.lontsi.rubberduckpulsarconnector.service.agents.SyntheseChatAgent;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SyntheseChatTool {

    @Autowired
    private SyntheseChatAgent syntheseChatAgent;

    @Tool(name = "syntheseChatTool", value = "")
    public String synthese(EnrichedMessageDto enrichedMessageDto) {
        log.info("=== SYNTHESE CHAT START ===");
        log.info("Mode assistance: {}", enrichedMessageDto.mode());
        log.info("Contenu: '{}'", enrichedMessageDto.content().length() > 100 ?
                enrichedMessageDto.content().substring(0, 100) + "..." : enrichedMessageDto.content());
        log.info("donnees enrichis : {} :", enrichedMessageDto.enrichment());

        try {
            String response = syntheseChatAgent.respond(enrichedMessageDto);
            log.info("=== SYNTHESE SUCCESS ===");
            return response;
        } catch (Exception e) {
            log.error("=== ORCHESTRATION ERROR ===", e);
            return "error when generating response";
        }

    }
}