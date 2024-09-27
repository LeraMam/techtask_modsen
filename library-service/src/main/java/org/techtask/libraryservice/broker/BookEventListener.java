package org.techtask.libraryservice.broker;

import org.techtask.libraryservice.event.BookEvent;

public interface BookEventListener {
    void createLibraryBookDTO(BookEvent book) ;

    void deleteLibraryBookDTO(BookEvent book) ;
}
