package com.lontsi.rubberduckpulsarconnector.dto;

public record EnrichedMessageDto(
        String content,
        AssistanceMode mode,
        EnrichmentData enrichment
) {
}