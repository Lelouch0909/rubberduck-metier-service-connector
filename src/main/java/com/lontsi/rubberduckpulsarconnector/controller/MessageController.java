package com.lontsi.rubberduckpulsarconnector.controller;

import com.lontsi.rubberduckpulsarconnector.controller.api.IMessageApi;
import com.lontsi.rubberduckpulsarconnector.dto.MessageConsumerDto;
import com.lontsi.rubberduckpulsarconnector.service.IProcessServiceMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.reactive.client.api.MessageResult;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController implements IMessageApi {

    private final IProcessServiceMessage processServiceMessage;

    @Override
    public Flux<MessageResult<Void>> handleMessage(Flux<Message<MessageConsumerDto>> messages) {
        return messages
                .flatMap((message ->
                {
                    try {
                        MessageConsumerDto messageConsumerDto = message.getValue();

                        if (messageConsumerDto != null) {
                            return processServiceMessage.processMessage(messageConsumerDto)
                                    .doOnSuccess(unused -> log.info("✅ Successfully processed: {}", messageConsumerDto))
                                    .doOnError(error -> log.error("❌ Error processing message: {}", messageConsumerDto, error))
                                    .then(Mono.just(MessageResult.acknowledge(message))) // ACK après traitement
                                    .onErrorReturn(MessageResult.negativeAcknowledge(message)); // NACK en cas d'erreur


                        }

                        return Mono.just(MessageResult.acknowledge(message));

                    } catch (Exception e) {
                        log.error("❌ Error while consuming message", e);
                        return Mono.just(MessageResult.negativeAcknowledge(message));

                    }

                }));

    }
}
