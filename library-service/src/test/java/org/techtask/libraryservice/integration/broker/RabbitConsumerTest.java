package org.techtask.libraryservice.integration.broker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.techtask.libraryservice.broker.impl.RabbitConsumerImpl;
import org.techtask.libraryservice.db.entity.LibraryBookEntity;
import org.techtask.libraryservice.db.repository.LibraryBookRepository;
import org.techtask.libraryservice.event.BookEvent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RabbitConsumerTest {
    @Autowired
    private RabbitConsumerImpl rabbitConsumerImpl;

    @Autowired
    private LibraryBookRepository libraryBookRepository;

    @AfterEach
    void cleanUp() {
        libraryBookRepository.deleteAll();
    }

    @Test
    public void createLibraryBookDtoTest() {
        List<LibraryBookEntity> list = libraryBookRepository.findAll();
        assertEquals(0, list.size());

        BookEvent bookEvent = new BookEvent(1L);
        rabbitConsumerImpl.createLibraryBookDTO(bookEvent);

        List<LibraryBookEntity> libraryBookEntities = libraryBookRepository.findAll();
        assertEquals(1, libraryBookEntities.size());
        LibraryBookEntity book = libraryBookEntities.get(0);
        assertNotNull(book.getId());
        assertEquals(bookEvent.getBookId(), book.getBookId());
    }

    @Test
    public void deleteLibraryBookDtoTest() {
        LibraryBookEntity book = LibraryBookEntity.builder().bookId(1L).build();
        libraryBookRepository.save(book);

        List<LibraryBookEntity> list = libraryBookRepository.findAll();
        assertEquals(1, list.size());

        BookEvent bookEvent = new BookEvent(1L);
        rabbitConsumerImpl.deleteLibraryBookDTO(bookEvent);

        List<LibraryBookEntity> libraryBookEntities = libraryBookRepository.findAll();
        assertEquals(0, libraryBookEntities.size());
    }
}
