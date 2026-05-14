package com.library.repository;

import com.library.domain.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, UUID> {
    
    @Query("SELECT b FROM Borrowing b WHERE b.book.id = :bookId AND b.returnedAt IS NULL")
    Optional<Borrowing> findActiveBorrowingByBookId(@Param("bookId") UUID bookId);
}
