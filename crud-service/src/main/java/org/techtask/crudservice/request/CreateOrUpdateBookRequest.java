package org.techtask.crudservice.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateOrUpdateBookRequest(@NotNull String isbn,
                                        String title, String genre,
                                        String description, String author) {
}
