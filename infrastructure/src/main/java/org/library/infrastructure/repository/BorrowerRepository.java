package org.library.infrastructure.repository;

import org.library.domain.model.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, String> {
    Optional<Borrower> findByEmail(String email);
    boolean existsByEmail(String email);
}
