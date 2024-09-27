package org.techtask.crudservice.integration.broker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.techtask.crudservice.broker.impl.RabbitProducerImpl;
import org.techtask.crudservice.db.entity.BookEntity;
import org.techtask.crudservice.db.repository.BookRepository;
import org.techtask.crudservice.event.BookEvent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RabbitProducerTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private RabbitProducerImpl rabbitProducerImpl;
    @MockBean
    private RabbitTemplate rabbitTemplate;
    @Captor
    private ArgumentCaptor<BookEvent> bookEventCaptor;
    @Captor
    private ArgumentCaptor<String> exchangeNameCaptor;
    @Captor
    private ArgumentCaptor<String> keyNameCaptor;

    @Value("${rabbitmq-config.exchange}")
    private String exchangeName;

    @Value("${rabbitmq-config.routing-key.create}")
    private String keyCreateName;

    @Value("${rabbitmq-config.routing-key.delete}")
    private String keyDeleteName;

    @AfterEach
    void cleanUp() {
        bookRepository.deleteAll();
    }

    @Test
    public void sendMessageSuccessTest() {
        BookEntity entity = BookEntity.builder().isbn("6748-3772-212").build();
        BookEntity createdBook = bookRepository.save(entity);

        List<BookEntity> bookEntityList = bookRepository.findAll();
        assertEquals(1, bookEntityList.size());
        BookEntity book = bookEntityList.get(0);
        assertNotNull(book.getId());
        assertEquals(entity.getIsbn(), createdBook.getIsbn());

        BookEvent bookEvent = new BookEvent(book.getId());
        rabbitProducerImpl.sendCreateMessage(bookEvent);

        verify(rabbitTemplate).convertAndSend(exchangeNameCaptor.capture(), keyNameCaptor.capture(), bookEventCaptor.capture());
        BookEvent bookCreatedEvent = bookEventCaptor.getValue();
        assertNotNull(bookCreatedEvent);
        assertEquals(book.getId(), bookCreatedEvent.getBookId());
        String resultExchangeName = exchangeNameCaptor.getValue();
        assertEquals(exchangeName, resultExchangeName);
        String resultKeyName = keyNameCaptor.getValue();
        assertEquals(keyCreateName, resultKeyName);
    }

    @Test
    public void deleteMessageSuccessTest() {
        BookEntity entity = BookEntity.builder().isbn("6748-3772-212").build();
        BookEntity createdBook = bookRepository.save(entity);

        List<BookEntity> bookEntityList = bookRepository.findAll();
        assertEquals(1, bookEntityList.size());
        BookEntity book = bookEntityList.get(0);
        assertNotNull(book.getId());
        assertEquals(entity.getIsbn(), createdBook.getIsbn());

        bookRepository.delete(book);

        List<BookEntity> bookDeleteEntityList = bookRepository.findAll();
        assertEquals(0, bookDeleteEntityList.size());

        BookEvent bookEvent = new BookEvent(book.getId());
        rabbitProducerImpl.sendDeleteMessage(bookEvent);

        verify(rabbitTemplate).convertAndSend(exchangeNameCaptor.capture(), keyNameCaptor.capture(), bookEventCaptor.capture());
        BookEvent bookCreatedEvent = bookEventCaptor.getValue();
        assertNotNull(bookCreatedEvent);
        assertEquals(book.getId(), bookCreatedEvent.getBookId());
        String resultExchangeName = exchangeNameCaptor.getValue();
        assertEquals(exchangeName, resultExchangeName);
        String resultKeyName = keyNameCaptor.getValue();
        assertEquals(keyDeleteName, resultKeyName);
    }
}
