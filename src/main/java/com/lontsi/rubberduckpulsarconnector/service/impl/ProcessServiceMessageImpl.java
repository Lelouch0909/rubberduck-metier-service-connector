package com.lontsi.rubberduckpulsarconnector.service.impl;

import com.lontsi.rubberduckpulsarconnector.dto.MessageConsumerDto;
import com.lontsi.rubberduckpulsarconnector.dto.MessageDto;
import com.lontsi.rubberduckpulsarconnector.service.IMessageProducerService;
import com.lontsi.rubberduckpulsarconnector.service.IProcessServiceMessage;
import com.lontsi.rubberduckpulsarconnector.service.agents.RouterAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@Service
@Slf4j
public class ProcessServiceMessageImpl implements IProcessServiceMessage {

    @Autowired
    private  IMessageProducerService messageProducer;

    @Autowired
    private  RouterAgent routerAgent;

    public Flux<String> process(MessageDto userRequest) {
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

        Flux<String> resultFlux = process(messageDto).concatWith(Mono.just("[END]"));

        return messageProducer.sendMessage(
                requestDto.id_discussion(),
                requestDto.principal(),
                resultFlux
        );
    }
}


