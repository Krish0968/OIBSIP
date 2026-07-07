package com.oasis.nexuslibrary.service;

import com.oasis.nexuslibrary.dto.BookDto;
import com.oasis.nexuslibrary.dto.IssueDto;
import com.oasis.nexuslibrary.dto.ReservationDto;
import com.oasis.nexuslibrary.entity.*;
import com.oasis.nexuslibrary.repository.BookRepository;
import com.oasis.nexuslibrary.repository.IssueRepository;
import com.oasis.nexuslibrary.repository.MemberRepository;
import com.oasis.nexuslibrary.repository.ReservationRepository;
import com.oasis.nexuslibrary.service.impl.IssueServiceImpl;
import com.oasis.nexuslibrary.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LibraryServiceTests {

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private IssueServiceImpl issueService;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Book testBook;
    private Member testMember;
    private Issue testIssue;
    private Reservation testReservation;

    @BeforeEach
    public void setup() {
        // Configure fine rate in service dynamically (usually injected via @Value)
        ReflectionTestUtils.setField(issueService, "fineRate", 10.0);
        ReflectionTestUtils.setField(reservationService, "expiryDays", 3);

        Author author = new Author(1L, "Robert C. Martin", "Uncle Bob");
        Category category = new Category(1L, "Technology", "Software engineering");
        
        testBook = new Book(1L, "9780132350884", "Clean Code", author, category, 
                            "Prentice Hall", "English", 2008, 5, 5, "T-01", 
                            BookStatus.AVAILABLE, "Summary desc");

        testMember = new Member(1L, "Krish Sharma", "krish@mail.com", "+91 9876543210", 
                                "hashedPassword", Role.MEMBER, LocalDate.now(), 
                                "New Delhi", MemberStatus.ACTIVE);

        testIssue = new Issue(1L, testBook, testMember, null, null, null, 0.0, 0, IssueStatus.REQUESTED);
        
        testReservation = new Reservation(1L, testBook, testMember, LocalDateTime.now(), ReservationStatus.PENDING);
    }

    @Test
    public void testBorrowRequestSubmitsSuccessfully() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(issueRepository.countByMemberIdAndStatus(1L, IssueStatus.ISSUED)).thenReturn(0L);
        when(issueRepository.countByMemberIdAndStatus(1L, IssueStatus.OVERDUE)).thenReturn(0L);
        
        // Mock save
        when(issueRepository.save(any(Issue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IssueDto result = issueService.requestBorrow(1L, 1L);

        assertNotNull(result);
        assertEquals(IssueStatus.REQUESTED, result.getStatus());
        assertEquals("Clean Code", result.getBookTitle());
        assertEquals("Krish Sharma", result.getMemberName());
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    public void testApproveBorrowDecreasesStockAndStartsCheckout() {
        when(issueRepository.findById(1L)).thenReturn(Optional.of(testIssue));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(issueRepository.save(any(Issue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int initialStock = testBook.getAvailableCopies();

        IssueDto result = issueService.approveBorrow(1L);

        assertNotNull(result);
        assertEquals(IssueStatus.ISSUED, result.getStatus());
        assertEquals(LocalDate.now(), result.getIssueDate());
        assertEquals(LocalDate.now().plusDays(14), result.getDueDate());
        
        // Check stock decrease
        assertEquals(initialStock - 1, testBook.getAvailableCopies());
        verify(bookRepository, times(1)).save(testBook);
        verify(issueRepository, times(1)).save(testIssue);
    }

    @Test
    public void testApproveReturnCalculatesFineCorrectly() {
        // Set issue as checked out, but overdue by 5 days
        LocalDate issueDate = LocalDate.now().minusDays(19);
        LocalDate dueDate = LocalDate.now().minusDays(5);
        
        testIssue.setStatus(IssueStatus.RETURN_REQUESTED);
        testIssue.setIssueDate(issueDate);
        testIssue.setDueDate(dueDate);

        when(issueRepository.findById(1L)).thenReturn(Optional.of(testIssue));
        when(issueRepository.save(any(Issue.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        int initialStock = testBook.getAvailableCopies();

        IssueDto result = issueService.approveReturn(1L);

        assertNotNull(result);
        assertEquals(IssueStatus.RETURNED, result.getStatus());
        assertEquals(LocalDate.now(), result.getReturnDate());
        
        // Fine is ₹10 per day, so 5 days late = ₹50 fine
        assertEquals(5, result.getLateDays());
        assertEquals(50.0, result.getFine());

        // Check stock increase
        assertEquals(initialStock + 1, testBook.getAvailableCopies());
    }

    @Test
    public void testApproveReservationHoldsBookCopies() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int initialStock = testBook.getAvailableCopies();

        ReservationDto result = reservationService.approveReservation(1L);

        assertNotNull(result);
        assertEquals(ReservationStatus.APPROVED, result.getStatus());
        assertEquals(initialStock - 1, testBook.getAvailableCopies());
    }
}
