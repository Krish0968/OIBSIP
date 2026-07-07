package com.oasis.nexuslibrary.service.impl;

import com.oasis.nexuslibrary.dto.ReservationDto;
import com.oasis.nexuslibrary.entity.*;
import com.oasis.nexuslibrary.exception.BadRequestException;
import com.oasis.nexuslibrary.exception.ResourceNotFoundException;
import com.oasis.nexuslibrary.mapper.ReservationMapper;
import com.oasis.nexuslibrary.repository.BookRepository;
import com.oasis.nexuslibrary.repository.IssueRepository;
import com.oasis.nexuslibrary.repository.MemberRepository;
import com.oasis.nexuslibrary.repository.ReservationRepository;
import com.oasis.nexuslibrary.service.ReservationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final IssueRepository issueRepository;

    @Value("${library.reservation.expiry-days:3}")
    private int expiryDays;

    public ReservationServiceImpl(ReservationRepository reservationRepository, BookRepository bookRepository, 
                                  MemberRepository memberRepository, IssueRepository issueRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.issueRepository = issueRepository;
    }

    @Override
    public ReservationDto createReservation(Long memberId, Long bookId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        if (member.getStatus() == MemberStatus.SUSPENDED) {
            throw new BadRequestException("Your account is suspended. You cannot reserve books.");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        // Check if already reserved
        boolean alreadyReserved = member.getReservations().stream()
                .anyMatch(r -> r.getBook().getId().equals(bookId) && 
                        (r.getStatus() == ReservationStatus.PENDING || r.getStatus() == ReservationStatus.APPROVED));
        if (alreadyReserved) {
            throw new BadRequestException("You already have an active reservation for this book.");
        }

        // Check if already checked out
        boolean alreadyCheckedOut = member.getIssues().stream()
                .anyMatch(i -> i.getBook().getId().equals(bookId) && 
                        (i.getStatus() == IssueStatus.ISSUED || i.getStatus() == IssueStatus.OVERDUE || i.getStatus() == IssueStatus.RETURN_REQUESTED));
        if (alreadyCheckedOut) {
            throw new BadRequestException("You already have this book checked out or requested.");
        }

        Reservation reservation = Reservation.builder()
                .book(book)
                .member(member)
                .status(ReservationStatus.PENDING)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationMapper.toDto(savedReservation);
    }

    @Override
    public ReservationDto approveReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BadRequestException("Only pending reservations can be approved.");
        }

        Book book = reservation.getBook();
        if (book.getAvailableCopies() <= 0) {
            throw new BadRequestException("Cannot approve reservation. The book is currently out of stock.");
        }

        // Hold a copy of the book
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        book.updateStatusBasedOnAvailability();
        bookRepository.save(book);

        // Update status and reset reservation date to mark the beginning of the hold period
        reservation.setStatus(ReservationStatus.APPROVED);
        reservation.setReservationDate(LocalDateTime.now());

        Reservation updatedReservation = reservationRepository.save(reservation);
        return ReservationMapper.toDto(updatedReservation);
    }

    @Override
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));

        if (reservation.getStatus() != ReservationStatus.PENDING && reservation.getStatus() != ReservationStatus.APPROVED) {
            throw new BadRequestException("Only pending or approved reservations can be cancelled. Current status: " + reservation.getStatus());
        }

        // If it was approved, we held a copy, so we must release it
        if (reservation.getStatus() == ReservationStatus.APPROVED) {
            Book book = reservation.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            book.updateStatusBasedOnAvailability();
            bookRepository.save(book);
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    @Override
    public void fulfillReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));

        if (reservation.getStatus() != ReservationStatus.APPROVED && reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BadRequestException("Cannot fulfill reservation. Current status: " + reservation.getStatus());
        }

        Member member = reservation.getMember();
        Book book = reservation.getBook();

        // If it was pending, we need to verify and decrement stock. If approved, stock was already decremented.
        if (reservation.getStatus() == ReservationStatus.PENDING) {
            if (book.getAvailableCopies() <= 0) {
                throw new BadRequestException("Cannot fulfill. The book is out of stock.");
            }
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            book.updateStatusBasedOnAvailability();
            bookRepository.save(book);
        }

        // Create checkout record
        Issue issue = Issue.builder()
                .book(book)
                .member(member)
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(IssueStatus.ISSUED)
                .fine(0.0)
                .lateDays(0)
                .build();

        issueRepository.save(issue);

        // Finalize reservation
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDto> findFiltered(String search, ReservationStatus status, Pageable pageable) {
        return reservationRepository.findReservationsFiltered(search, status, pageable)
                .map(ReservationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDto> findMemberReservations(Long memberId, Pageable pageable) {
        return reservationRepository.findByMemberId(memberId, pageable)
                .map(ReservationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> findMemberActiveReservations(Long memberId) {
        return reservationRepository.findByMemberId(memberId).stream()
                .filter(r -> r.getStatus() == ReservationStatus.PENDING || r.getStatus() == ReservationStatus.APPROVED)
                .map(ReservationMapper::toDto)
                .toList();
    }

    @Override
    public void checkExpiredReservations() {
        LocalDateTime expirationThreshold = LocalDateTime.now().minusDays(expiryDays);
        
        // Find approved reservations that have exceeded the hold period limit
        List<Reservation> expiredReservations = reservationRepository
                .findByStatusAndReservationDateBefore(ReservationStatus.APPROVED, expirationThreshold);

        for (Reservation reservation : expiredReservations) {
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);

            // Release book hold
            Book book = reservation.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            book.updateStatusBasedOnAvailability();
            bookRepository.save(book);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveReservations() {
        return reservationRepository.countByStatus(ReservationStatus.PENDING) + reservationRepository.countByStatus(ReservationStatus.APPROVED);
    }
}
