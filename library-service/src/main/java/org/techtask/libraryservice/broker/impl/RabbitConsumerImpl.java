package org.techtask.libraryservice.broker.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.techtask.libraryservice.broker.BookEventListener;
import org.techtask.libraryservice.event.BookEvent;
import org.techtask.libraryservice.service.impl.LibraryBookServiceImpl;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitConsumerImpl implements BookEventListener {
    private final LibraryBookServiceImpl libraryBookServiceImpl;

    @Value("${rabbitmq-config.queue.create}")
    private String queueCreateName;

    @Value("${rabbitmq-config.queue.delete}")
    private String queueDeleteName;

    @Bean
    public Queue queueCreate() {
        return new Queue(queueCreateName, true);
    }

    @Bean
    public Queue queueDelete() {
        return new Queue(queueDeleteName, true);
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @RabbitListener(queues = "${rabbitmq-config.queue.create}")
    public void createLibraryBookDTO(BookEvent book) {
        try {
            log.info("Book created at crud service: {}", book);
            libraryBookServiceImpl.createLibraryBook(book);
        } catch (Exception e) {
            log.error("Book creating failed: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "${rabbitmq-config.queue.delete}")
    public void deleteLibraryBookDTO(BookEvent book) {
        try {
            log.info("Book deleted at crud service: {}", book);
            libraryBookServiceImpl.deleteLibraryBook(book);
        } catch (Exception e) {
            log.error("Book deleting failed: {}", e.getMessage());
        }
    }

}
