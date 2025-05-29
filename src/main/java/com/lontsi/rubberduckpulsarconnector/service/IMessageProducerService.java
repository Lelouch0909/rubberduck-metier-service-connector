package com.lontsi.rubberduckpulsarconnector.service;

import com.lontsi.rubberduckpulsarconnector.dto.MessageProducerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IMessageProducerService {


    Mono<Void> sendMessage(String idDiscussion, String principal, Flux<String> fragments);
}
