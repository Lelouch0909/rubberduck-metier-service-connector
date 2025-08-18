package com.lontsi.rubberduckpulsarconnector.dto;

import com.lontsi.rubberduckpulsarconnector.models.type.Model;
import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.pulsar.annotation.PulsarMessage;
@PulsarMessage(schemaType = SchemaType.JSON)
public record MessageConsumerDto(String principal, String id_discussion, String content, Model model, AssistantTier tier,
                                 AssistanceMode mode) {
}
