package org.techtask.crudservice.controller.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.techtask.crudservice.controller.BookController;
import org.techtask.crudservice.dto.BookDTO;
import org.techtask.crudservice.request.CreateOrUpdateBookRequest;
import org.techtask.crudservice.service.BookService;

import java.util.List;

@RestController()
@RequiredArgsConstructor
public class BookControllerImpl implements BookController {
    private final BookService bookService;

    public List<BookDTO> getAllBooks() {

        return bookService.getAllBooks();
    }

    public BookDTO getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    public BookDTO getBookByISBN(@PathVariable String isbn) {
        return bookService.getBookByIsbn(isbn);
    }

    public BookDTO addBook(@RequestBody @Valid CreateOrUpdateBookRequest request) {
        return bookService.createBook(request);
    }

    public BookDTO updateBook(@PathVariable Long id, @RequestBody @Valid CreateOrUpdateBookRequest request) {
        return bookService.updateBook(id, request);
    }

    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}
