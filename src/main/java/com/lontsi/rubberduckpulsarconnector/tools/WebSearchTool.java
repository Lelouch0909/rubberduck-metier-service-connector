package com.lontsi.rubberduckpulsarconnector.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class WebSearchTool {

    @Tool(name = "webSearch", value = "Searches the web for up-to-date info. Returns raw text only.")
    public String search(String query) {
        // Impl réelle: appel API externe, agrégation — ici placeholder.
        return "websearch successfully"; // NE RIEN SYNTHÉTISER ICI.
    }
}