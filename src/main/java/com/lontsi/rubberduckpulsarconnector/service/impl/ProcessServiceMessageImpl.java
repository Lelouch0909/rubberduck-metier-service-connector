package com.lontsi.rubberduckpulsarconnector.service.impl;

import com.lontsi.rubberduckpulsarconnector.dto.MessageConsumerDto;
import com.lontsi.rubberduckpulsarconnector.service.IMessageProducerService;
import com.lontsi.rubberduckpulsarconnector.service.IProcessServiceMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessServiceMessageImpl implements IProcessServiceMessage {

    private final IMessageProducerService messageProducer;


    @Override
    public Mono<Void> processMessage(MessageConsumerDto requestDto) {
        Flux<String> fragments = Flux.just("fragment1", "fragment2", "fragment3");

        // Envoyer chaque fragment
        return fragments
                .flatMap(fragment -> {

                    return messageProducer.sendMessage(requestDto.id_discussion(), requestDto.principal(), fragments);
                })

                .then();
    }
}
