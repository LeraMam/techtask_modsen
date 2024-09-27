package org.techtask.crudservice.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.techtask.crudservice.broker.impl.RabbitProducerImpl;
import org.techtask.crudservice.db.entity.BookEntity;
import org.techtask.crudservice.db.repository.BookRepository;
import org.techtask.crudservice.event.BookEvent;
import org.techtask.crudservice.request.CreateOrUpdateBookRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookControllerImplTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookRepository bookRepository;
    @MockBean
    private RabbitProducerImpl rabbitProducerImpl;
    @Captor
    private ArgumentCaptor<BookEvent> bookEventCaptor;

    @AfterEach
    void cleanUp() {
        bookRepository.deleteAll();
    }

    @Test
    @WithMockUser(value = "spring")
    void postRequestTestSuccess() throws Exception {
        CreateOrUpdateBookRequest request = CreateOrUpdateBookRequest.builder()
                .isbn("978-5-7320-1162-3").title("book title")
                .genre("book genre").description("book description")
                .author("book author").build();

        assertNotNull(rabbitProducerImpl);
        ResultActions resultActions = mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        List<BookEntity> bookEntityList = bookRepository.findAll();
        assertEquals(1, bookEntityList.size());
        BookEntity bookEntity = bookEntityList.get(0);
        assertNotNull(bookEntity.getId());
        assertEquals(request.isbn(), bookEntity.getIsbn());
        assertEquals(request.title(), bookEntity.getTitle());
        assertEquals(request.genre(), bookEntity.getGenre());
        assertEquals(request.description(), bookEntity.getDescription());
        assertEquals(request.author(), bookEntity.getAuthor());

        resultActions.andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(bookEntity.getId()))
                .andExpect(jsonPath("$.isbn").value(bookEntity.getIsbn()))
                .andExpect(jsonPath("$.title").value(bookEntity.getTitle()))
                .andExpect(jsonPath("$.genre").value(bookEntity.getGenre()))
                .andExpect(jsonPath("$.description").value(request.description()))
                .andExpect(jsonPath("$.author").value(bookEntity.getAuthor()));

        verify(rabbitProducerImpl, times(1)).sendCreateMessage(bookEventCaptor.capture());
        BookEvent bookEvent = bookEventCaptor.getValue();
        assertNotNull(bookEvent);
        assertEquals(bookEntity.getId(), bookEvent.getBookId());
    }

    @Test
    @WithMockUser(value = "spring")
    void postRequestTestFail() throws Exception {
        CreateOrUpdateBookRequest request = CreateOrUpdateBookRequest.builder()
                .title("book title").genre("book genre")
                .description("book description").author("book author")
                .build();
        assertNotNull(rabbitProducerImpl);

        ResultActions resultActions = mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        List<BookEntity> bookEntityList = bookRepository.findAll();
        assertEquals(0, bookEntityList.size());

        verify(rabbitProducerImpl, Mockito.never()).sendCreateMessage(bookEventCaptor.capture());
        resultActions.andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    @WithMockUser(value = "spring")
    public void getRequestTestSuccess() throws Exception {
        List<CreateOrUpdateBookRequest> requestList = List.of(
                CreateOrUpdateBookRequest.builder()
                        .isbn("978-5-7320-1162-3").title("book title1")
                        .genre("book genre1").description("book description1")
                        .author("book author1").build(),
                CreateOrUpdateBookRequest.builder()
                        .isbn("535-0-4351-72-211").title("book title2")
                        .genre("book genre2").description("book description2")
                        .author("book author2").build()
        );
        for (CreateOrUpdateBookRequest request : requestList) {
            mockMvc.perform(post("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(200));
        }
        ResultActions resultActions = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        List<BookEntity> bookEntityList = bookRepository.findAll();
        assertEquals(requestList.size(), bookEntityList.size());

        for (int i = 0; i < requestList.size(); i++) {
            assertNotNull(bookEntityList.get(i).getId());
            assertEquals(requestList.get(i).isbn(), bookEntityList.get(i).getIsbn());
            assertEquals(requestList.get(i).title(), bookEntityList.get(i).getTitle());
            assertEquals(requestList.get(i).genre(), bookEntityList.get(i).getGenre());
            assertEquals(requestList.get(i).description(), bookEntityList.get(i).getDescription());
            assertEquals(requestList.get(i).author(), bookEntityList.get(i).getAuthor());

            resultActions.andDo(print())
                    .andExpect(jsonPath("$[" + i + "].isbn").value(requestList.get(i).isbn()))
                    .andExpect(jsonPath("$[" + i + "].title").value(requestList.get(i).title()))
                    .andExpect(jsonPath("$[" + i + "].genre").value(requestList.get(i).genre()))
                    .andExpect(jsonPath("$[" + i + "].description").value(requestList.get(i).description()))
                    .andExpect(jsonPath("$[" + i + "].author").value(requestList.get(i).author()));
        }
    }

    @Test
    @WithMockUser(value = "spring")
    public void getRequestTestFail() throws Exception {
        List<CreateOrUpdateBookRequest> requestList = List.of(
                CreateOrUpdateBookRequest.builder()
                        .title("book title1")
                        .genre("book genre1").description("book description1")
                        .author("book author1").build(),
                CreateOrUpdateBookRequest.builder()
                        .isbn("535-0-4351-72-211").title("book title2")
                        .genre("book genre2").description("book description2")
                        .author("book author2").build()
        );
        for (CreateOrUpdateBookRequest request : requestList) {
            mockMvc.perform(post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));
        }

        MvcResult mvcResult = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        List<CreateOrUpdateBookRequest> bookList = objectMapper.readValue(
                json, new TypeReference<>() {
                });
        assertNotEquals(requestList.size(), bookList.size());
    }

    @Test
    @WithMockUser(value = "spring")
    void putRequestTestSuccess() throws Exception {
        BookEntity entity = BookEntity.builder().isbn("6748-3772-212").build();
        CreateOrUpdateBookRequest request = CreateOrUpdateBookRequest.builder()
                .isbn("978-5-7320-1162-3").title("book title")
                .genre("book genre").description("book description")
                .author("book author").build();

        BookEntity existingBook = bookRepository.save(entity);

        ResultActions resultActions = mockMvc.perform(put("/books/{id}", existingBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        List<BookEntity> bookEntityList = bookRepository.findAll();
        assertEquals(1, bookEntityList.size());
        BookEntity book = bookEntityList.get(0);
        assertEquals(existingBook.getId(), book.getId());
        assertEquals(request.isbn(), book.getIsbn());
        assertEquals(request.title(), book.getTitle());
        assertEquals(request.genre(), book.getGenre());
        assertEquals(request.description(), book.getDescription());
        assertEquals(request.author(), book.getAuthor());

        resultActions.andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.isbn").value(request.isbn()))
                .andExpect(jsonPath("$.title").value(request.title()))
                .andExpect(jsonPath("$.genre").value(request.genre()))
                .andExpect(jsonPath("$.description").value(request.description()))
                .andExpect(jsonPath("$.author").value(request.author()));
    }

    @Test
    @WithMockUser(value = "spring")
    void putRequestTestFail() throws Exception {
        BookEntity entity = BookEntity.builder().isbn("6748-3772-212").build();
        CreateOrUpdateBookRequest request = CreateOrUpdateBookRequest.builder()
                .title("book title")
                .genre("book genre").description("book description")
                .author("book author").build();

        BookEntity existingBook = bookRepository.save(entity);

        mockMvc.perform(put("/books/{id}", existingBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(400));

        List<BookEntity> bookEntityList = bookRepository.findAll();
        assertEquals(1, bookEntityList.size());
        BookEntity book = bookEntityList.get(0);

        assertNotEquals(request.isbn(), book.getIsbn());
        assertNotEquals(request.title(), book.getTitle());
        assertNotEquals(request.genre(), book.getGenre());
        assertNotEquals(request.description(), book.getDescription());
        assertNotEquals(request.author(), book.getAuthor());
    }

    @Test
    @WithMockUser(value = "spring")
    void putRequestTestEntityNotFound() throws Exception {
        BookEntity entity = BookEntity.builder().isbn("6748-3772-212").build();
        CreateOrUpdateBookRequest request = CreateOrUpdateBookRequest.builder()
                .isbn("535-0-4351-72-211").title("book title")
                .genre("book genre").description("book description")
                .author("book author").build();
        bookRepository.save(entity);

        ResultActions resultActions = mockMvc.perform(put("/books/{id}", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(404));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(responseBody.contains("Book with id 3 not found"));
    }

    @Test
    @WithMockUser(value = "spring")
    void deleteRequestTestSuccess() throws Exception {
        BookEntity entity = BookEntity.builder().isbn("6748-3772-212").build();
        assertNotNull(rabbitProducerImpl);

        BookEntity savedEntity = bookRepository.save(entity);
        List<BookEntity> bookEntityList = bookRepository.findAll();
        assertEquals(1, bookEntityList.size());
        assertEquals(savedEntity.getId(), bookEntityList.get(0).getId());

        mockMvc.perform(delete("/books/{id}", savedEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(rabbitProducerImpl, times(1)).sendDeleteMessage(bookEventCaptor.capture());
        BookEvent bookEvent = bookEventCaptor.getValue();
        assertNotNull(bookEvent);
        assertEquals(savedEntity.getId(), bookEvent.getBookId());

    }

    @Test
    @WithMockUser(value = "spring")
    void deleteRequestTestFail() throws Exception {
        BookEntity entity = BookEntity.builder().isbn("6748-3772-212").build();
        assertNotNull(rabbitProducerImpl);
        bookRepository.save(entity);
        ResultActions resultActions = mockMvc.perform(delete("/books/{id}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(404));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(responseBody.contains("Book with id 5 not found"));

        verify(rabbitProducerImpl, Mockito.never()).sendDeleteMessage(bookEventCaptor.capture());
    }

}
