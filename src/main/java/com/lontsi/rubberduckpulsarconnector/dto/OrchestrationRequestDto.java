package com.lontsi.rubberduckpulsarconnector.dto;


public record OrchestrationRequestDto(
        String content,
        AssistanceMode mode
) {
}