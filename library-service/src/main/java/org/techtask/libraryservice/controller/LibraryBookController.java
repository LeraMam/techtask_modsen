package org.techtask.libraryservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.techtask.libraryservice.dto.LibraryBookDTO;
import org.techtask.libraryservice.request.UpdateLibraryBookRequest;

import java.util.List;

@SecurityRequirement(name = "JWT")
@RequestMapping("/library")
public interface LibraryBookController {
    @GetMapping
    List<LibraryBookDTO> getAvailableBooks();

    @PutMapping("/{id}")
    LibraryBookDTO updateBook(@PathVariable Long id, @RequestBody UpdateLibraryBookRequest request);
}
