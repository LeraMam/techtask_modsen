package org.techtask.libraryservice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.techtask.libraryservice.db.entity.LibraryBookEntity;
import org.techtask.libraryservice.db.entity.LibraryBookStatus;
import org.techtask.libraryservice.db.repository.LibraryBookRepository;
import org.techtask.libraryservice.request.UpdateLibraryBookRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LibraryBookControllerImplTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LibraryBookRepository libraryBookRepository;

    @AfterEach
    void cleanUp() {
        libraryBookRepository.deleteAll();
    }

    @Test
    @WithMockUser(value = "spring")
    public void getRequestTestSuccess() throws Exception {
        List<LibraryBookEntity> requestList = List.of(
                LibraryBookEntity.builder().bookId(1L).libraryBookStatus(LibraryBookStatus.AVAILABLE).build(),
                LibraryBookEntity.builder().bookId(2L).libraryBookStatus(LibraryBookStatus.CHECKED_OUT).build(),
                LibraryBookEntity.builder().bookId(3L).libraryBookStatus(LibraryBookStatus.AVAILABLE).build()
        );
        libraryBookRepository.saveAll(requestList);

        ResultActions resultActions = mockMvc.perform(get("/library")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));

        List<LibraryBookEntity> bookEntityList = libraryBookRepository.findAll();
        List<LibraryBookEntity> availableBooks = bookEntityList.stream()
                .filter(book -> book.getLibraryBookStatus() == LibraryBookStatus.AVAILABLE)
                .toList();
        assertEquals(2, availableBooks.size());

        for (int i = 0; i < availableBooks.size(); i++) {
            resultActions.andDo(print())
                    .andExpect(jsonPath("$[" + i + "].bookId").value(availableBooks.get(i).getBookId()))
                    .andExpect(jsonPath("$[" + i + "].libraryBookStatus").value(LibraryBookStatus.AVAILABLE.toString()));
        }
    }

    @Test
    @WithMockUser(value = "spring")
    void putRequestTestSuccess() throws Exception {
        LibraryBookEntity entity = LibraryBookEntity.builder()
                .bookId(1L)
                .libraryBookStatus(LibraryBookStatus.AVAILABLE)
                .build();
        UpdateLibraryBookRequest request = UpdateLibraryBookRequest.builder()
                .bookId(1L).libraryBookStatus(LibraryBookStatus.CHECKED_OUT)
                .build();

        LibraryBookEntity existingBook = libraryBookRepository.save(entity);

        ResultActions resultActions = mockMvc.perform(put("/library/{id}", existingBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        List<LibraryBookEntity> bookEntityList = libraryBookRepository.findAll();
        assertEquals(1, bookEntityList.size());
        LibraryBookEntity book = bookEntityList.get(0);
        assertEquals(existingBook.getId(), book.getId());
        assertEquals(request.bookId(), book.getBookId());
        assertEquals(request.borrowedDate(), book.getBorrowedDate());
        assertEquals(request.dueDate(), book.getDueDate());
        assertEquals(request.libraryBookStatus(), book.getLibraryBookStatus());

        resultActions.andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.bookId").value(request.bookId()))
                .andExpect(jsonPath("$.borrowedDate").value(request.borrowedDate()))
                .andExpect(jsonPath("$.dueDate").value(request.dueDate()))
                .andExpect(jsonPath("$.libraryBookStatus").value(request.libraryBookStatus().toString()));
    }

    @Test
    @WithMockUser(value = "spring")
    void putRequestTestFail() throws Exception {
        LibraryBookEntity entity = LibraryBookEntity.builder()
                .bookId(1L).borrowedDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .libraryBookStatus(LibraryBookStatus.AVAILABLE)
                .build();
        UpdateLibraryBookRequest request = UpdateLibraryBookRequest.builder()
                .libraryBookStatus(LibraryBookStatus.CHECKED_OUT)
                .build();

        LibraryBookEntity existingBook = libraryBookRepository.save(entity);

        mockMvc.perform(put("/books/{id}", existingBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(404));

        List<LibraryBookEntity> bookEntityList = libraryBookRepository.findAll();
        assertEquals(1, bookEntityList.size());
        LibraryBookEntity book = bookEntityList.get(0);

        assertNotEquals(request.bookId(), book.getBookId());
        assertNotEquals(request.borrowedDate(), book.getBorrowedDate());
        assertNotEquals(request.dueDate(), book.getDueDate());
        assertNotEquals(request.libraryBookStatus(), book.getLibraryBookStatus());
    }
}
