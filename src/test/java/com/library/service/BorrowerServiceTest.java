package com.library.service;

import com.library.domain.Borrower;
import com.library.dto.BorrowerRequest;
import com.library.dto.BorrowerResponse;
import com.library.repository.BorrowerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowerServiceTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BorrowerService borrowerService;

    private Borrower sampleBorrower;
    private BorrowerRequest borrowerRequest;

    @BeforeEach
    void setUp() {
        sampleBorrower = new Borrower();
        sampleBorrower.setId(UUID.randomUUID());
        sampleBorrower.setName("John Doe");
        sampleBorrower.setEmail("john.doe@example.com");
        sampleBorrower.setCreatedAt(LocalDateTime.now());

        borrowerRequest = new BorrowerRequest();
        borrowerRequest.setName("John Doe");
        borrowerRequest.setEmail("john.doe@example.com");
    }

    @Test
    void createBorrower_ShouldReturnBorrowerResponse() {
        when(borrowerRepository.findByEmail(borrowerRequest.getEmail())).thenReturn(Optional.empty());
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(sampleBorrower);

        BorrowerResponse response = borrowerService.createBorrower(borrowerRequest);

        assertNotNull(response);
        assertEquals(sampleBorrower.getId(), response.getId());
        assertEquals(sampleBorrower.getName(), response.getName());
        assertEquals(sampleBorrower.getEmail(), response.getEmail());

        verify(borrowerRepository, times(1)).findByEmail(borrowerRequest.getEmail());
        verify(borrowerRepository, times(1)).save(any(Borrower.class));
    }

    @Test
    void createBorrower_WhenEmailAlreadyExists_ShouldThrowException() {
        when(borrowerRepository.findByEmail(borrowerRequest.getEmail())).thenReturn(Optional.of(sampleBorrower));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowerService.createBorrower(borrowerRequest);
        });

        assertEquals("Borrower with email already exists: " + borrowerRequest.getEmail(), exception.getMessage());
        verify(borrowerRepository, times(1)).findByEmail(borrowerRequest.getEmail());
        verify(borrowerRepository, never()).save(any(Borrower.class));
    }

    @Test
    void getAllBorrowers_ShouldReturnListOfBorrowers() {
        Borrower anotherBorrower = new Borrower();
        anotherBorrower.setId(UUID.randomUUID());
        anotherBorrower.setName("Jane Doe");
        anotherBorrower.setEmail("jane.doe@example.com");
        anotherBorrower.setCreatedAt(LocalDateTime.now());

        when(borrowerRepository.findAll()).thenReturn(Arrays.asList(sampleBorrower, anotherBorrower));

        List<BorrowerResponse> responses = borrowerService.getAllBorrowers();

        assertEquals(2, responses.size());
        verify(borrowerRepository, times(1)).findAll();
    }

    @Test
    void getBorrowerById_WhenBorrowerExists_ShouldReturnBorrowerResponse() {
        UUID borrowerId = sampleBorrower.getId();
        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(sampleBorrower));

        BorrowerResponse response = borrowerService.getBorrowerById(borrowerId);

        assertNotNull(response);
        assertEquals(borrowerId, response.getId());
        verify(borrowerRepository, times(1)).findById(borrowerId);
    }

    @Test
    void getBorrowerById_WhenBorrowerNotFound_ShouldThrowException() {
        UUID borrowerId = UUID.randomUUID();
        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowerService.getBorrowerById(borrowerId);
        });

        assertEquals("Borrower not found with id: " + borrowerId, exception.getMessage());
        verify(borrowerRepository, times(1)).findById(borrowerId);
    }
}
