package org.techtask.crudservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq-config.queue.create}")
    private String queueCreateName;

    @Value("${rabbitmq-config.queue.delete}")
    private String queueDeleteName;

    @Value("${rabbitmq-config.exchange}")
    private String exchangeName;

    @Value("${rabbitmq-config.routing-key.create}")
    private String keyCreateName;

    @Value("${rabbitmq-config.routing-key.delete}")
    private String keyDeleteName;

    @Bean
    public Queue queueCreate() {
        return new Queue(queueCreateName, true);
    }

    @Bean
    public Queue queueDelete() {
        return new Queue(queueDeleteName, true);
    }

    @Bean
    public Exchange exchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Binding bindingCreate(@Qualifier("queueCreate") Queue queueCreate, Exchange exchange) {
        return BindingBuilder.bind(queueCreate).to(exchange).with(keyCreateName).noargs();
    }

    @Bean
    public Binding bindingDelete(@Qualifier("queueDelete") Queue queueDelete, Exchange exchange) {
        return BindingBuilder.bind(queueDelete).to(exchange).with(keyDeleteName).noargs();
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
