package com.lontsi.rubberduckpulsarconnector.tools;

import com.lontsi.rubberduckpulsarconnector.dto.MessageDto;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SaveTool {

    @Tool(name = "save", value = "Saves data with structured information")
    public String save(MessageDto messageDto) {
        log.info(">>> SaveTool.save appelé avec : {}", messageDto);
        return "bien sauvegarde";
    }

}