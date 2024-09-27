package org.techtask.libraryservice.unit.controller;

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
import org.techtask.libraryservice.controller.impl.LibraryBookControllerImpl;
import org.techtask.libraryservice.db.entity.LibraryBookStatus;
import org.techtask.libraryservice.dto.LibraryBookDTO;
import org.techtask.libraryservice.request.UpdateLibraryBookRequest;
import org.techtask.libraryservice.service.LibraryBookService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(LibraryBookControllerImpl.class)
public class LibraryBookControllerImplTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibraryBookService libraryBookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(value = "spring")
    public void getAvailableBooksTest() throws Exception {
        LibraryBookDTO book1 = LibraryBookDTO.builder()
                .bookId(1L)
                .libraryBookStatus(LibraryBookStatus.AVAILABLE)
                .build();
        LibraryBookDTO book2 = LibraryBookDTO.builder()
                .bookId(2L)
                .libraryBookStatus(LibraryBookStatus.AVAILABLE)
                .build();
        List<LibraryBookDTO> booksList = new ArrayList<>();
        booksList.add(book1);
        booksList.add(book2);
        given(libraryBookService.getAvailableBooks()).willReturn(booksList);

        ResultActions response = mockMvc.perform(get("/library"));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()").value(booksList.size()));
    }

    @Test
    @WithMockUser(value = "spring")
    public void updateBookTest() throws Exception {
        Long bookId = 1L;
        UpdateLibraryBookRequest request = UpdateLibraryBookRequest.builder()
                .bookId(1L)
                .libraryBookStatus(LibraryBookStatus.CHECKED_OUT)
                .build();
        LibraryBookDTO updatedBook = LibraryBookDTO.builder()
                .bookId(bookId)
                .libraryBookStatus(LibraryBookStatus.CHECKED_OUT)
                .build();

        given(libraryBookService.updateLibraryBook(bookId, request)).willReturn(updatedBook);

        ResultActions response = mockMvc.perform(put("/library/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.libraryBookStatus").value(updatedBook.libraryBookStatus().toString()));
    }
}