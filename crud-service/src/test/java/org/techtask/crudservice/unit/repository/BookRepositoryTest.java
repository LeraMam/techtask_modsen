package org.techtask.crudservice.unit.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.techtask.crudservice.db.entity.BookEntity;
import org.techtask.crudservice.db.repository.BookRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ActiveProfiles("test")
public class BookRepositoryTest {
    @Autowired
    private final BookRepository bookRepository;

    @Test
    public void saveBookTest() {
        BookEntity entity = BookEntity.builder().isbn("6748-3772-212").build();
        BookEntity savedEntity = bookRepository.save(entity);
        assertNotNull(savedEntity.getId());
    }

    @Test
    public void getBookTest() {
        BookEntity entity = BookEntity.builder().isbn("6748-3772-212").build();
        BookEntity savedEntity = bookRepository.save(entity);
        BookEntity bookEntity = bookRepository.findById(savedEntity.getId()).get();
        assertEquals("6748-3772-212", bookEntity.getIsbn());
    }

    @Test
    public void getBookEntityByIsbn() {
        BookEntity entity = BookEntity.builder().isbn("6748-3772-212").build();
        bookRepository.save(entity);
        Optional<BookEntity> bookEntity = bookRepository.findByIsbn("6748-3772-212");
        assertTrue(bookEntity.isPresent());
        assertEquals("6748-3772-212", bookEntity.get().getIsbn());
    }

    @Test
    public void readBooksTest() {
        List<BookEntity> requestList = List.of(
                BookEntity.builder().isbn("6748-3772-212").build(),
                BookEntity.builder().isbn("15748-7479-02").build()
        );
        bookRepository.saveAll(requestList);
        List<BookEntity> entityList = bookRepository.findAll();
        assertEquals(2, entityList.size());
    }

    @Test
    public void updateBookTest() {
        BookEntity entity = BookEntity.builder().isbn("6748-3772-212").build();
        BookEntity savedEntity = bookRepository.save(entity);
        BookEntity readEntity = bookRepository.findById(savedEntity.getId()).get();
        readEntity.setIsbn("000-111-222");
        BookEntity updatedEntity = bookRepository.save(readEntity);
        assertEquals("000-111-222", updatedEntity.getIsbn());
    }

    @Test
    public void deleteBookTest() {
        BookEntity entity = BookEntity.builder().isbn("6748-3772-212").build();
        bookRepository.save(entity);
        bookRepository.deleteById(1L);
        Optional<BookEntity> bookOptional = bookRepository.findById(1L);
        assertThat(bookOptional).isEmpty();
    }
}
