package org.techtask.libraryservice.service;

import org.techtask.libraryservice.dto.LibraryBookDTO;
import org.techtask.libraryservice.event.BookEvent;
import org.techtask.libraryservice.request.UpdateLibraryBookRequest;

import java.util.List;

public interface LibraryBookService {
    List<LibraryBookDTO> getAvailableBooks();

    LibraryBookDTO createLibraryBook(BookEvent event);

    LibraryBookDTO updateLibraryBook(Long id, UpdateLibraryBookRequest request);

    void deleteLibraryBook(BookEvent event);
}
