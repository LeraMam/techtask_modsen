package org.techtask.crudservice.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.techtask.crudservice.broker.BookEventNotifier;
import org.techtask.crudservice.db.entity.BookEntity;
import org.techtask.crudservice.db.repository.BookRepository;
import org.techtask.crudservice.dto.BookDTO;
import org.techtask.crudservice.event.BookEvent;
import org.techtask.crudservice.mapper.BookMapper;
import org.techtask.crudservice.request.CreateOrUpdateBookRequest;
import org.techtask.crudservice.service.impl.BookServiceImpl;

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
public class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookEventNotifier bookEventNotifier;

    @Test
    public void getAllBooks() {
        BookEntity entity = BookEntity.builder()
                .isbn("6748-3772-212").title("book title")
                .genre("book genre").description("book description")
                .author("book author").build();
        BookDTO dto = BookDTO.builder()
                .isbn("6748-3772-212").title("book title")
                .genre("book genre").description("book description")
                .author("book author").build();

        when(bookRepository.findAll()).thenReturn(List.of(entity));
        when(bookMapper.mapEntitiesToDtoList(any())).thenReturn(List.of(dto));
        List<BookDTO> availableBooks = bookService.getAllBooks();

        assertNotNull(availableBooks);
        assertEquals(1, availableBooks.size());

        BookDTO returnedBook = availableBooks.get(0);
        assertEquals("6748-3772-212", returnedBook.isbn());
        assertEquals("book title", returnedBook.title());
        assertEquals("book genre", returnedBook.genre());
        assertEquals("book description", returnedBook.description());
        assertEquals("book author", returnedBook.author());
    }

    @Test
    public void getBookByIdTest() {
        Long bookId = 1L;
        BookEntity entity = BookEntity.builder()
                .id(bookId).isbn("6748-3772-212")
                .title("book title").genre("book genre")
                .description("book description").author("book author")
                .build();
        BookDTO dto = BookDTO.builder()
                .isbn("6748-3772-212").title("book title")
                .genre("book genre").description("book description")
                .author("book author").build();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(entity));
        when(bookMapper.mapEntityToDto(entity)).thenReturn(dto);

        BookDTO returnedDto = bookService.getBookById(bookId);
        assertNotNull(returnedDto);
        assertEquals("6748-3772-212", returnedDto.isbn());
        assertEquals("book title", returnedDto.title());
        assertEquals("book genre", returnedDto.genre());
        assertEquals("book description", returnedDto.description());
        assertEquals("book author", returnedDto.author());
    }

    @Test
    public void getBookByIsbnTest() {
        String isbn = "6748-3772-212";
        BookEntity entity = BookEntity.builder()
                .isbn(isbn).title("book title")
                .genre("book genre").description("book description")
                .author("book author").build();
        BookDTO dto = BookDTO.builder()
                .isbn(isbn).title("book title")
                .genre("book genre").description("book description")
                .author("book author").build();

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(entity));
        when(bookMapper.mapEntityToDto(entity)).thenReturn(dto);

        BookDTO returnedDto = bookService.getBookByIsbn(isbn);
        assertNotNull(returnedDto);
        assertEquals(isbn, returnedDto.isbn());
        assertEquals("book title", returnedDto.title());
        assertEquals("book genre", returnedDto.genre());
        assertEquals("book description", returnedDto.description());
        assertEquals("book author", returnedDto.author());
    }

    @Test
    public void createBookTest() {
        CreateOrUpdateBookRequest request = CreateOrUpdateBookRequest.builder()
                .isbn("6748-3772-212").title("book title")
                .genre("book genre").description("book description")
                .author("book author").build();
        BookEntity entity = BookEntity.builder()
                .id(1L)
                .isbn("6748-3772-212").title("book title")
                .genre("book genre").description("book description")
                .author("book author").build();
        BookDTO dto = BookDTO.builder()
                .id(1L).isbn("6748-3772-212")
                .title("book title").genre("book genre")
                .description("book description")
                .author("book author").build();

        when(bookMapper.mapRequestToEntity(request)).thenReturn(entity);
        when(bookRepository.save(any())).thenReturn(entity);
        when(bookMapper.mapEntityToDto(entity)).thenReturn(dto);

        BookDTO savedDto = bookService.createBook(request);
        assertNotNull(savedDto);

        assertEquals("6748-3772-212", savedDto.isbn());
        assertEquals("book title", savedDto.title());
        assertEquals("book genre", savedDto.genre());
        assertEquals("book description", savedDto.description());
        assertEquals("book author", savedDto.author());
        verify(bookEventNotifier, times(1)).sendCreateMessage(any(BookEvent.class));
    }

    @Test
    public void updateBook() {
        BookEntity existingEntity = BookEntity.builder()
                .id(1L).isbn("6748-3772-212")
                .title("original title").genre("original genre")
                .description("original description").author("original author")
                .build();
        CreateOrUpdateBookRequest request = CreateOrUpdateBookRequest.builder()
                .isbn("6748-3772-212").title("updated title")
                .genre("updated genre").description("updated description")
                .author("updated author").build();
        BookDTO expectedDto = BookDTO.builder()
                .id(1L).isbn("6748-3772-212")
                .title("updated title").genre("updated genre")
                .description("updated description").author("updated author")
                .build();

        given(bookRepository.findById(1L)).willReturn(Optional.of(existingEntity));
        given(bookMapper.updateBook(existingEntity, request)).willReturn(existingEntity);
        given(bookRepository.save(existingEntity)).willReturn(existingEntity);
        given(bookMapper.mapEntityToDto(existingEntity)).willReturn(expectedDto);

        BookDTO updatedDto = bookService.updateBook(1L, request);
        assertNotNull(updatedDto);
        assertThat(updatedDto.id()).isEqualTo(1L);
        assertThat(updatedDto.title()).isEqualTo("updated title");
        assertThat(updatedDto.genre()).isEqualTo("updated genre");
        assertThat(updatedDto.description()).isEqualTo("updated description");
        assertThat(updatedDto.author()).isEqualTo("updated author");
    }

    @Test
    public void deleteBook() {
        BookEntity entity = BookEntity.builder()
                .id(1L).isbn("6748-3772-212")
                .title("some title").genre("some genre")
                .description("some description").author("some author")
                .build();
        Long bookId = 1L;
        BookEvent event = BookEvent.builder().bookId(bookId).build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(entity));

        assertDoesNotThrow(() -> bookService.deleteBook(bookId));
        verify(bookEventNotifier, times(1)).sendDeleteMessage(event); // Проверяем отправку события
        verify(bookRepository, times(1)).delete(entity);
    }
}
