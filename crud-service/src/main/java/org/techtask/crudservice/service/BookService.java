package org.techtask.crudservice.service;

import org.techtask.crudservice.dto.BookDTO;
import org.techtask.crudservice.request.CreateOrUpdateBookRequest;

import java.util.List;

public interface BookService {

    List<BookDTO> getAllBooks();

    BookDTO getBookById(Long id);

    BookDTO getBookByIsbn(String isbn);

    BookDTO createBook(CreateOrUpdateBookRequest request);

    BookDTO updateBook(Long id, CreateOrUpdateBookRequest request);

    void deleteBook(Long id);
}
