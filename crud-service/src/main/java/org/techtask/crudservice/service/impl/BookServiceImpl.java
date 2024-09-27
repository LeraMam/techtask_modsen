package org.techtask.crudservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.techtask.crudservice.broker.BookEventNotifier;
import org.techtask.crudservice.db.entity.BookEntity;
import org.techtask.crudservice.db.repository.BookRepository;
import org.techtask.crudservice.dto.BookDTO;
import org.techtask.crudservice.event.BookEvent;
import org.techtask.crudservice.exception.NotFoundException;
import org.techtask.crudservice.mapper.BookMapper;
import org.techtask.crudservice.request.CreateOrUpdateBookRequest;
import org.techtask.crudservice.service.BookService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookEventNotifier bookEventNotifier;

    @Transactional(readOnly = true)
    public List<BookDTO> getAllBooks() {
        return bookMapper.mapEntitiesToDtoList(bookRepository.findAll());
    }

    @Transactional(readOnly = true)
    public BookDTO getBookById(Long id) {
        BookEntity bookEntity = bookRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Book with id " + id + " not found"));
        return bookMapper.mapEntityToDto(bookEntity);
    }

    @Transactional(readOnly = true)
    public BookDTO getBookByIsbn(String isbn) {
        BookEntity bookEntity = bookRepository.findByIsbn(isbn).orElseThrow(()
                -> new NotFoundException("Book with isbn " + isbn + " not found"));
        return bookMapper.mapEntityToDto(bookEntity);
    }

    @Transactional
    public BookDTO createBook(CreateOrUpdateBookRequest request) {
        BookEntity bookEntity = bookRepository.save(bookMapper.mapRequestToEntity(request));
        BookEvent event = new BookEvent(bookEntity.getId());
        bookEventNotifier.sendCreateMessage(event);
        return bookMapper.mapEntityToDto(bookEntity);
    }

    @Transactional
    public BookDTO updateBook(Long id, CreateOrUpdateBookRequest request) {
        BookEntity updateBook = bookRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Book with id " + id + " not found"));
        BookEntity entity = bookMapper.updateBook(updateBook, request);
        return bookMapper.mapEntityToDto(bookRepository.save(entity));
    }

    @Transactional
    public void deleteBook(Long id) {
        BookEntity bookEntity = bookRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Book with id " + id + " not found"));
        BookEvent event = new BookEvent(bookEntity.getId());
        bookEventNotifier.sendDeleteMessage(event);
        bookRepository.delete(bookEntity);
    }
}
