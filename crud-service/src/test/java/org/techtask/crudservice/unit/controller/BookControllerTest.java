package org.techtask.crudservice.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.techtask.crudservice.controller.impl.BookControllerImpl;
import org.techtask.crudservice.dto.BookDTO;
import org.techtask.crudservice.request.CreateOrUpdateBookRequest;
import org.techtask.crudservice.service.BookService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(BookControllerImpl.class)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(value = "spring")
    public void getAllBooksTest() throws Exception {
        BookDTO book1 = BookDTO.builder()
                .id(1L).isbn("6748-3772-212")
                .title("book title 1").genre("book genre")
                .description("book description").author("book author")
                .build();
        BookDTO book2 = BookDTO.builder()
                .id(2L).isbn("6748-3772-213")
                .title("book title 2").genre("book genre")
                .description("book description").author("book author")
                .build();
        List<BookDTO> booksList = List.of(book1, book2);
        given(bookService.getAllBooks()).willReturn(booksList);

        ResultActions response = mockMvc.perform(get("/books"));
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()").value(booksList.size()));
    }

    @Test
    @WithMockUser(value = "spring")
    public void getBookByIdTest() throws Exception {
        Long bookId = 1L;
        BookDTO book = BookDTO.builder()
                .id(bookId).isbn("6748-3772-212")
                .title("book title").genre("book genre")
                .description("book description").author("book author")
                .build();
        given(bookService.getBookById(bookId)).willReturn(book);

        ResultActions response = mockMvc.perform(get("/books/{id}", bookId));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.isbn").value(book.isbn()))
                .andExpect(jsonPath("$.title").value(book.title()))
                .andExpect(jsonPath("$.genre").value(book.genre()))
                .andExpect(jsonPath("$.description").value(book.description()))
                .andExpect(jsonPath("$.author").value(book.author()));
    }

    @Test
    @WithMockUser(value = "spring")
    public void getBookByIsbnTest() throws Exception {
        String isbn = "6748-3772-212";
        BookDTO book = BookDTO.builder()
                .id(1L).isbn(isbn)
                .title("book title").genre("book genre")
                .description("book description").author("book author")
                .build();
        given(bookService.getBookByIsbn(isbn)).willReturn(book);

        ResultActions response = mockMvc.perform(get("/books/isbn/{isbn}", isbn));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(book.id()))
                .andExpect(jsonPath("$.isbn").value(book.isbn()))
                .andExpect(jsonPath("$.title").value(book.title()))
                .andExpect(jsonPath("$.genre").value(book.genre()))
                .andExpect(jsonPath("$.description").value(book.description()))
                .andExpect(jsonPath("$.author").value(book.author()));
    }

    @Test
    @WithMockUser(value = "spring")
    public void addBookTest() throws Exception {
        CreateOrUpdateBookRequest request = CreateOrUpdateBookRequest.builder()
                .isbn("6748-3772-212").title("new book title")
                .genre("new book genre").description("new book description")
                .author("new book author").build();

        BookDTO createdBook = BookDTO.builder()
                .id(1L).isbn("6748-3772-212")
                .title("new book title").genre("new book genre")
                .description("new book description").author("new book author")
                .build();
        given(bookService.createBook(request)).willReturn(createdBook);

        ResultActions response = mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(createdBook.id()))
                .andExpect(jsonPath("$.isbn").value(createdBook.isbn()))
                .andExpect(jsonPath("$.title").value(createdBook.title()))
                .andExpect(jsonPath("$.genre").value(createdBook.genre()))
                .andExpect(jsonPath("$.description").value(createdBook.description()))
                .andExpect(jsonPath("$.author").value(createdBook.author()));
    }

    @Test
    @WithMockUser(value = "spring")
    public void updateBookTest() throws Exception {
        Long bookId = 1L;
        CreateOrUpdateBookRequest request = CreateOrUpdateBookRequest.builder()
                .isbn("6748-3772-212").title("title")
                .genre("genre").description("description")
                .author("author").build();
        BookDTO updatedBook = BookDTO.builder()
                .id(bookId).isbn("6748-3772-212")
                .title("updated title").genre("updated genre")
                .description("updated description").author("updated author")
                .build();
        given(bookService.updateBook(bookId, request)).willReturn(updatedBook);

        ResultActions response = mockMvc.perform(put("/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value(updatedBook.title()))
                .andExpect(jsonPath("$.genre").value(updatedBook.genre()))
                .andExpect(jsonPath("$.description").value(updatedBook.description()))
                .andExpect(jsonPath("$.author").value(updatedBook.author()));
    }

    @Test
    @WithMockUser(value = "spring")
    public void deleteBookTest() throws Exception {
        Long bookId = 1L;
        doNothing().when(bookService).deleteBook(bookId);

        mockMvc.perform(delete("/books/{id}", bookId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookService, times(1)).deleteBook(bookId);
    }
}
