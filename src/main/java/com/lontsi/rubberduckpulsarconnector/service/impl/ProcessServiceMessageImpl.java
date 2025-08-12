package com.lontsi.rubberduckpulsarconnector.service.impl;

import com.lontsi.rubberduckpulsarconnector.dto.MessageConsumerDto;
import com.lontsi.rubberduckpulsarconnector.dto.MessageDto;
import com.lontsi.rubberduckpulsarconnector.service.IMessageProducerService;
import com.lontsi.rubberduckpulsarconnector.service.IProcessServiceMessage;
import com.lontsi.rubberduckpulsarconnector.service.agents.RouterAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessServiceMessageImpl implements IProcessServiceMessage {

    private final IMessageProducerService messageProducer;
    private final RouterAgent routerAgent;

    private Flux<String> process(MessageDto userRequest) {
        return Mono.fromCallable(() -> routerAgent.routeAndProcess(userRequest))
                .subscribeOn(Schedulers.boundedElastic())
                .flux();
    }

    @Override
    public Mono<Void> processMessage(MessageConsumerDto requestDto) {
        MessageDto messageDto = new MessageDto(
                requestDto.content(),
                requestDto.tier(),
                requestDto.mode()
        );

        Flux<String> resultFlux = process(messageDto);

        return messageProducer.sendMessage(
                requestDto.id_discussion(),
                requestDto.principal(),
                resultFlux
        );
    }
}


