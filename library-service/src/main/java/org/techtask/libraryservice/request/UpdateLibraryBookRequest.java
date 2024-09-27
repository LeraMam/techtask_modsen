package org.techtask.libraryservice.request;

import lombok.Builder;
import lombok.Setter;
import org.techtask.libraryservice.db.entity.LibraryBookStatus;

import java.time.LocalDate;

@Builder
public record UpdateLibraryBookRequest(Long bookId,
                                       LocalDate borrowedDate,
                                       LocalDate dueDate,
                                       LibraryBookStatus libraryBookStatus) {
}
