package org.techtask.libraryservice.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.techtask.libraryservice.db.entity.LibraryBookEntity;
import org.techtask.libraryservice.db.entity.LibraryBookStatus;
import org.techtask.libraryservice.db.repository.LibraryBookRepository;
import org.techtask.libraryservice.dto.LibraryBookDTO;
import org.techtask.libraryservice.event.BookEvent;
import org.techtask.libraryservice.mapper.LibraryBookMapper;
import org.techtask.libraryservice.request.UpdateLibraryBookRequest;
import org.techtask.libraryservice.service.impl.LibraryBookServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class LibraryBookServiceImplTest {
    @Mock
    private LibraryBookRepository libraryBookRepository;
    @Mock
    private LibraryBookMapper libraryBookMapper;
    @InjectMocks
    private LibraryBookServiceImpl libraryBookService;

    @Test
    public void getAllAvailableBooks() {
        LibraryBookEntity entity = LibraryBookEntity.builder()
                .bookId(1L)
                .libraryBookStatus(LibraryBookStatus.AVAILABLE)
                .build();
        LibraryBookDTO dto = LibraryBookDTO.builder()
                .bookId(1L)
                .libraryBookStatus(LibraryBookStatus.AVAILABLE)
                .build();

        when(libraryBookRepository.findLibraryBookEntitiesByLibraryBookStatus(LibraryBookStatus.AVAILABLE))
                .thenReturn(List.of(entity));
        when(libraryBookMapper.mapEntitiesToDtoList(any()))
                .thenReturn(List.of(dto));

        List<LibraryBookDTO> availableBooks = libraryBookService.getAvailableBooks();
        assertNotNull(availableBooks);
        assertEquals(1, availableBooks.size());
        assertEquals(1L, availableBooks.get(0).bookId());
    }

    @Test
    public void createBookTest() {
        LibraryBookEntity entity = LibraryBookEntity.builder()
                .bookId(1L)
                .libraryBookStatus(LibraryBookStatus.AVAILABLE)
                .build();
        BookEvent event = BookEvent.builder().bookId(1L).build();
        LibraryBookDTO dto = LibraryBookDTO.builder()
                .bookId(1L)
                .libraryBookStatus(LibraryBookStatus.AVAILABLE)
                .build();

        when(libraryBookRepository.save(any())).thenReturn(entity);
        when(libraryBookMapper.mapEntityToDto(entity)).thenReturn(dto);

        LibraryBookDTO savedDto = libraryBookService.createLibraryBook(event);
        assertNotNull(savedDto);
        assertEquals(1L, savedDto.bookId());
        assertEquals(LibraryBookStatus.AVAILABLE, savedDto.libraryBookStatus());
    }


    @Test
    public void updateBook() {
        LibraryBookEntity existingEntity = LibraryBookEntity.builder()
                .bookId(1L)
                .libraryBookStatus(LibraryBookStatus.AVAILABLE)
                .build();
        UpdateLibraryBookRequest request = UpdateLibraryBookRequest.builder()
                .bookId(1L)
                .libraryBookStatus(LibraryBookStatus.CHECKED_OUT)
                .build();
        LibraryBookDTO expectedDto = LibraryBookDTO.builder()
                .bookId(1L)
                .libraryBookStatus(LibraryBookStatus.CHECKED_OUT)
                .build();

        given(libraryBookRepository.findById(1L)).willReturn(Optional.of(existingEntity));
        given(libraryBookMapper.updateBook(existingEntity, request)).willReturn(existingEntity);
        given(libraryBookRepository.save(existingEntity)).willReturn(existingEntity);
        given(libraryBookMapper.mapEntityToDto(existingEntity)).willReturn(expectedDto);

        LibraryBookDTO updatedDto = libraryBookService.updateLibraryBook(1L, request);
        assertNotNull(updatedDto);
        assertThat(updatedDto.bookId()).isEqualTo(1L);
        assertThat(updatedDto.libraryBookStatus()).isEqualTo(LibraryBookStatus.CHECKED_OUT);
    }

    @Test
    public void deleteBook() {
        LibraryBookEntity entity = LibraryBookEntity.builder()
                .bookId(1L)
                .libraryBookStatus(LibraryBookStatus.AVAILABLE)
                .build();
        BookEvent event = BookEvent.builder().bookId(1L).build();

        when(libraryBookRepository.findLibraryBookEntityByBookId(1L)).thenReturn(Optional.of(entity));

        assertDoesNotThrow(() -> libraryBookService.deleteLibraryBook(event));
        verify(libraryBookRepository, times(1)).delete(entity);
    }
}
