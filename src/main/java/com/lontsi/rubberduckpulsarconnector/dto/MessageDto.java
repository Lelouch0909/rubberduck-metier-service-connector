package com.lontsi.rubberduckpulsarconnector.dto;


public record MessageDto(
        String content,
        AssistantTier tier,
        AssistanceMode mode
) {
}