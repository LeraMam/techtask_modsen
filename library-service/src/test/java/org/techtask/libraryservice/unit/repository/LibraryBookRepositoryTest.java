package org.techtask.libraryservice.unit.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.techtask.libraryservice.db.entity.LibraryBookEntity;
import org.techtask.libraryservice.db.entity.LibraryBookStatus;
import org.techtask.libraryservice.db.repository.LibraryBookRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ActiveProfiles("test")
public class LibraryBookRepositoryTest {
    @Autowired
    private final LibraryBookRepository libraryBookRepository;

    @Test
    public void saveBookTest() {
        LibraryBookEntity entity = LibraryBookEntity.builder()
                .bookId(1L)
                .libraryBookStatus(LibraryBookStatus.AVAILABLE)
                .build();
        LibraryBookEntity savedEntity = libraryBookRepository.save(entity);
        assertNotNull(savedEntity.getId());
    }

    @Test
    public void getBookTest() {
        LibraryBookEntity entity = LibraryBookEntity.builder()
                .bookId(1L)
                .libraryBookStatus(LibraryBookStatus.AVAILABLE)
                .build();
        LibraryBookEntity savedEntity = libraryBookRepository.save(entity);

        LibraryBookEntity bookEntity = libraryBookRepository.findById(savedEntity.getId()).get();
        assertEquals(1L, bookEntity.getBookId());
    }

    @Test
    public void getBookEntityByBookIdTest() {
        LibraryBookEntity entity = LibraryBookEntity.builder()
                .bookId(1L)
                .libraryBookStatus(LibraryBookStatus.AVAILABLE)
                .build();
        libraryBookRepository.save(entity);

        Optional<LibraryBookEntity> bookEntity = libraryBookRepository.findLibraryBookEntityByBookId(1L);
        assertTrue(bookEntity.isPresent());
        assertEquals(1L, bookEntity.get().getBookId());
    }

    @Test
    public void getListOfBookTest() {
        List<LibraryBookEntity> requestList = List.of(
                LibraryBookEntity.builder().bookId(1L).libraryBookStatus(LibraryBookStatus.AVAILABLE).build(),
                LibraryBookEntity.builder().bookId(2L).libraryBookStatus(LibraryBookStatus.CHECKED_OUT).build(),
                LibraryBookEntity.builder().bookId(3L).libraryBookStatus(LibraryBookStatus.AVAILABLE).build()
        );
        libraryBookRepository.saveAll(requestList);
        List<LibraryBookEntity> entityList = libraryBookRepository.findAll();
        assertEquals(3, entityList.size());
    }

    @Test
    public void getListOfBookByLibraryBookStatusTest() {
        List<LibraryBookEntity> requestList = List.of(
                LibraryBookEntity.builder().bookId(1L).libraryBookStatus(LibraryBookStatus.AVAILABLE).build(),
                LibraryBookEntity.builder().bookId(2L).libraryBookStatus(LibraryBookStatus.CHECKED_OUT).build(),
                LibraryBookEntity.builder().bookId(3L).libraryBookStatus(LibraryBookStatus.AVAILABLE).build()
        );
        libraryBookRepository.saveAll(requestList);

        List<LibraryBookEntity> entityList = libraryBookRepository
                .findLibraryBookEntitiesByLibraryBookStatus(LibraryBookStatus.AVAILABLE);
        List<LibraryBookEntity> streamEntityList = libraryBookRepository
                .findAll()
                .stream()
                .filter(entity -> entity.getLibraryBookStatus() == LibraryBookStatus.AVAILABLE) // Фильтрация по статусу
                .collect(Collectors.toList());
        assertEquals(entityList.size(), streamEntityList.size());
        assertTrue(entityList.containsAll(streamEntityList));
    }

    @Test
    public void updateBookTest() {
        LibraryBookEntity entity = LibraryBookEntity.builder()
                .bookId(1L)
                .libraryBookStatus(LibraryBookStatus.AVAILABLE)
                .build();
        LibraryBookEntity savedEntity = libraryBookRepository.save(entity);
        LibraryBookEntity readEntity = libraryBookRepository.findById(savedEntity.getId()).get();
        readEntity.setBookId(1L);
        readEntity.setLibraryBookStatus(LibraryBookStatus.CHECKED_OUT);

        LibraryBookEntity updatedEntity = libraryBookRepository.save(readEntity);
        assertEquals(LibraryBookStatus.CHECKED_OUT, updatedEntity.getLibraryBookStatus());
        assertEquals(1L, updatedEntity.getBookId());
    }

    @Test
    public void deleteBookTest() {
        LibraryBookEntity entity = LibraryBookEntity.builder()
                .bookId(1L)
                .libraryBookStatus(LibraryBookStatus.AVAILABLE)
                .build();
        libraryBookRepository.save(entity);

        libraryBookRepository.deleteById(1L);
        Optional<LibraryBookEntity> bookOptional = libraryBookRepository.findById(1L);
        assertThat(bookOptional).isEmpty();
    }
}
