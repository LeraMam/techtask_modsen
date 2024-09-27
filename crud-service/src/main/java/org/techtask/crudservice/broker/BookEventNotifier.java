package org.techtask.crudservice.broker;

import org.techtask.crudservice.event.BookEvent;

public interface BookEventNotifier {
    void sendCreateMessage(BookEvent projectCreatedEvent) ;

    void sendDeleteMessage(BookEvent projectCreatedEvent);
}
