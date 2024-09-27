package org.techtask.crudservice.dto;

import lombok.Builder;

@Builder
public record BookDTO(Long id, String isbn,
                      String title, String genre,
                      String description, String author) {
}
