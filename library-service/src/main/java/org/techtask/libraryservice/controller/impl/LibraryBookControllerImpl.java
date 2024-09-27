package org.techtask.libraryservice.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.techtask.libraryservice.controller.LibraryBookController;
import org.techtask.libraryservice.dto.LibraryBookDTO;
import org.techtask.libraryservice.request.UpdateLibraryBookRequest;
import org.techtask.libraryservice.service.LibraryBookService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class LibraryBookControllerImpl implements LibraryBookController {
    private final LibraryBookService libraryBookService;

    public List<LibraryBookDTO> getAvailableBooks() {
        return libraryBookService.getAvailableBooks();
    }

    public LibraryBookDTO updateBook(@PathVariable Long id, @RequestBody UpdateLibraryBookRequest request) {
        return libraryBookService.updateLibraryBook(id, request);
    }
}
