package org.techtask.crudservice.broker.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.techtask.crudservice.broker.BookEventNotifier;
import org.techtask.crudservice.event.BookEvent;

@Getter
@Component
@RequiredArgsConstructor
public class RabbitProducerImpl implements BookEventNotifier {

    @Value("${rabbitmq-config.exchange}")
    private String exchangeName;

    @Value("${rabbitmq-config.routing-key.create}")
    private String keyCreateName;

    @Value("${rabbitmq-config.routing-key.delete}")
    private String keyDeleteName;

    private final RabbitTemplate rabbitTemplate;

    private final MessageConverter messageConverter;

    public void sendCreateMessage(BookEvent projectCreatedEvent) {
        rabbitTemplate.convertAndSend(exchangeName, keyCreateName, projectCreatedEvent);
    }

    public void sendDeleteMessage(BookEvent projectCreatedEvent) {
        rabbitTemplate.convertAndSend(exchangeName, keyDeleteName, projectCreatedEvent);
    }
}
