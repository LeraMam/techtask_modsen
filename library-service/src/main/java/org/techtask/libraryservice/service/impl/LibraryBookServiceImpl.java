package org.techtask.libraryservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.techtask.libraryservice.db.entity.LibraryBookEntity;
import org.techtask.libraryservice.db.entity.LibraryBookStatus;
import org.techtask.libraryservice.db.repository.LibraryBookRepository;
import org.techtask.libraryservice.dto.LibraryBookDTO;
import org.techtask.libraryservice.event.BookEvent;
import org.techtask.libraryservice.exception.BadRequestException;
import org.techtask.libraryservice.exception.NotFoundException;
import org.techtask.libraryservice.mapper.LibraryBookMapper;
import org.techtask.libraryservice.request.UpdateLibraryBookRequest;
import org.techtask.libraryservice.service.LibraryBookService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LibraryBookServiceImpl implements LibraryBookService {
    private final LibraryBookRepository libraryBookRepository;
    private final LibraryBookMapper libraryBookMapper;

    public List<LibraryBookDTO> getAvailableBooks() {
        return libraryBookMapper.mapEntitiesToDtoList(libraryBookRepository.
                findLibraryBookEntitiesByLibraryBookStatus(LibraryBookStatus.AVAILABLE));
    }

    @Transactional
    public LibraryBookDTO createLibraryBook(BookEvent event) {
        if (event.getBookId() == null) throw new BadRequestException("Book id is null");
        LibraryBookEntity libraryBookEntity = new LibraryBookEntity();
        libraryBookEntity.setBookId(event.getBookId());
        libraryBookEntity.setLibraryBookStatus(LibraryBookStatus.AVAILABLE);
        return libraryBookMapper.mapEntityToDto(libraryBookRepository.save(libraryBookEntity));
    }

    @Transactional
    public LibraryBookDTO updateLibraryBook(Long id, UpdateLibraryBookRequest request) {
        LibraryBookEntity updateBook = libraryBookRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Book with id " + id + " not found in library"));
        LibraryBookEntity entity = libraryBookMapper.updateBook(updateBook, request);
        return libraryBookMapper.mapEntityToDto(libraryBookRepository.save(entity));
    }

    @Transactional
    public void deleteLibraryBook(BookEvent event) {
        LibraryBookEntity bookEntity = libraryBookRepository.findLibraryBookEntityByBookId(event.getBookId()).orElseThrow(()
                -> new NotFoundException("Book with id " + event.getBookId() + " not found in library"));
        libraryBookRepository.delete(bookEntity);
    }
}
