package com.lontsi.rubberduckpulsarconnector.service.impl;


import com.lontsi.rubberduckpulsarconnector.dto.MessageProducerDto;
import com.lontsi.rubberduckpulsarconnector.service.IMessageProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.pulsar.reactive.core.ReactivePulsarTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageProducerServiceImpl implements IMessageProducerService {

    private static final int MAX_RETRIES = 3;
    private static final Duration INITIAL_BACKOFF = Duration.ofMillis(500);
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private final ReactivePulsarTemplate<MessageProducerDto> reactivePulsarTemplate;
    @Value("${pulsar.topic-responses}")
    private String TOPIC;

    @Override
    public Mono<Void> sendMessage(String idDiscussion, String principal, Flux<String> fragments) {

        return fragments
                .concatMap(fragment -> {
                    MessageProducerDto message = new MessageProducerDto(principal, idDiscussion, fragment);
                    return reactivePulsarTemplate
                            .newMessage(message)
                            .withTopic(TOPIC)
                            .withMessageCustomizer(msgBuilder -> msgBuilder.key(idDiscussion))
                            .send()
                            .timeout(TIMEOUT)
                            .retryWhen(Retry.backoff(MAX_RETRIES, INITIAL_BACKOFF))
                            .doOnSuccess(unused -> log.debug("✅ Sent fragment: {}", fragment))
                            .doOnError(err -> log.error("❌ Failed to send fragment: {}", fragment, err));
                })
                .then();
    }


}
