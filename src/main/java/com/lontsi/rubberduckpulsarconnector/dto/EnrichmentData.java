package com.lontsi.rubberduckpulsarconnector.dto;

import java.time.LocalDateTime;
import java.util.List;

public record EnrichmentData(
        String collectedInformation,
        List<String> toolsUsed,
        LocalDateTime processingTimestamp,
        String processingNote
) {
}