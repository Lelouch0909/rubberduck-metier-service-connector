package com.lontsi.rubberduckpulsarconnector.service;

import com.lontsi.rubberduckpulsarconnector.dto.MessageConsumerDto;
import reactor.core.publisher.Mono;

public interface IProcessServiceMessage {


    Mono<Void> processMessage(MessageConsumerDto requestDto);

}
