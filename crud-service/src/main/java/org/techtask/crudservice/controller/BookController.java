package org.techtask.crudservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.techtask.crudservice.dto.BookDTO;
import org.techtask.crudservice.request.CreateOrUpdateBookRequest;

import java.util.List;

@SecurityRequirement(name = "JWT")
@RequestMapping("/books")
public interface BookController {

    @GetMapping
    List<BookDTO> getAllBooks();

    @GetMapping("/{id}")
    BookDTO getBookById(@PathVariable Long id);

    @GetMapping("/isbn/{isbn}")
    BookDTO getBookByISBN(@PathVariable String isbn);

    @PostMapping
    BookDTO addBook(@RequestBody @Valid CreateOrUpdateBookRequest request);

    @PutMapping("/{id}")
    BookDTO updateBook(@PathVariable Long id, @RequestBody @Valid CreateOrUpdateBookRequest request);

    @DeleteMapping("/{id}")
    void deleteBook(@PathVariable Long id);
}
