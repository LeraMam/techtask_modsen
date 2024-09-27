package org.techtask.libraryservice.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.techtask.libraryservice.db.entity.LibraryBookEntity;
import org.techtask.libraryservice.db.entity.LibraryBookStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryBookRepository extends JpaRepository<LibraryBookEntity, Long> {
    List<LibraryBookEntity> findLibraryBookEntitiesByLibraryBookStatus(LibraryBookStatus libraryBookStatus);

    Optional<LibraryBookEntity> findLibraryBookEntityByBookId(Long bookId);
}
