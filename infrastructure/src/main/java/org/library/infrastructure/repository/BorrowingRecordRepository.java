package org.library.infrastructure.repository;

import org.library.domain.model.BorrowingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, String> {
    
    @Query("SELECT br FROM BorrowingRecord br WHERE br.book.id = :bookId AND br.returned = false")
    Optional<BorrowingRecord> findActiveBorrowingByBookId(@Param("bookId") String bookId);
}
