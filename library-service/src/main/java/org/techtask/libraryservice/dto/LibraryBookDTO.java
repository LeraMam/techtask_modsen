package org.techtask.libraryservice.dto;

import lombok.Builder;
import org.techtask.libraryservice.db.entity.LibraryBookStatus;

import java.time.LocalDate;

@Builder
public record LibraryBookDTO(Long id,
                             Long bookId,
                             LocalDate borrowedDate,
                             LocalDate dueDate,
                             LibraryBookStatus libraryBookStatus) {
}
