package com.lontsi.rubberduckpulsarconnector.controller.api;

import com.lontsi.rubberduckpulsarconnector.dto.MessageConsumerDto;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.schema.SchemaType;
import org.apache.pulsar.reactive.client.api.MessageResult;
import org.springframework.pulsar.reactive.config.annotation.ReactivePulsarListener;
import reactor.core.publisher.Flux;

public interface IMessageApi {


    @ReactivePulsarListener(subscriptionName = "discussion-subscription",
            topics = "discussions",
            stream = true,
            schemaType = SchemaType.JSON,
            subscriptionType = SubscriptionType.Shared

    )
    Flux<MessageResult<Void>> handleMessage(Flux<Message<MessageConsumerDto>> messages);
}
