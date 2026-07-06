package com.oasis.nexuslibrary.service.impl;

import com.oasis.nexuslibrary.dto.IssueDto;
import com.oasis.nexuslibrary.entity.*;
import com.oasis.nexuslibrary.exception.BadRequestException;
import com.oasis.nexuslibrary.exception.ResourceNotFoundException;
import com.oasis.nexuslibrary.mapper.IssueMapper;
import com.oasis.nexuslibrary.repository.BookRepository;
import com.oasis.nexuslibrary.repository.IssueRepository;
import com.oasis.nexuslibrary.repository.MemberRepository;
import com.oasis.nexuslibrary.service.IssueService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Value("${library.fine.rate-per-day:10.0}")
    private double fineRate;

    public IssueServiceImpl(IssueRepository issueRepository, BookRepository bookRepository, MemberRepository memberRepository) {
        this.issueRepository = issueRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public IssueDto requestBorrow(Long memberId, Long bookId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        if (member.getStatus() == MemberStatus.SUSPENDED) {
            throw new BadRequestException("Your account is suspended. You cannot borrow books.");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        if (book.getAvailableCopies() <= 0) {
            throw new BadRequestException("This book is currently out of stock. You can place a reservation request instead.");
        }

        // Verify no duplicate requests/checkouts
        boolean alreadyHas = member.getIssues().stream()
                .anyMatch(issue -> issue.getBook().getId().equals(bookId) && 
                        (issue.getStatus() == IssueStatus.REQUESTED || 
                         issue.getStatus() == IssueStatus.ISSUED || 
                         issue.getStatus() == IssueStatus.OVERDUE || 
                         issue.getStatus() == IssueStatus.RETURN_REQUESTED));
        if (alreadyHas) {
            throw new BadRequestException("You already have an active request or checkout for this book.");
        }

        // Maximum checkouts limit check (e.g. max 5 books)
        long activeCount = issueRepository.countByMemberIdAndStatus(memberId, IssueStatus.ISSUED)
                + issueRepository.countByMemberIdAndStatus(memberId, IssueStatus.OVERDUE);
        if (activeCount >= 5) {
            throw new BadRequestException("You have reached the maximum borrow limit of 5 books. Please return checked-out books first.");
        }

        Issue issue = Issue.builder()
                .book(book)
                .member(member)
                .status(IssueStatus.REQUESTED)
                .fine(0.0)
                .lateDays(0)
                .build();

        Issue savedIssue = issueRepository.save(issue);
        return IssueMapper.toDto(savedIssue);
    }

    @Override
    public IssueDto approveBorrow(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow request not found with id: " + issueId));

        if (issue.getStatus() != IssueStatus.REQUESTED) {
            throw new BadRequestException("Cannot approve this request because its status is: " + issue.getStatus());
        }

        Book book = issue.getBook();
        if (book.getAvailableCopies() <= 0) {
            throw new BadRequestException("Cannot approve request. The book is now out of stock.");
        }

        // Decrease stock
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        book.updateStatusBasedOnAvailability();
        bookRepository.save(book);

        // Update issue parameters
        issue.setIssueDate(LocalDate.now());
        issue.setDueDate(LocalDate.now().plusDays(14)); // 14 days standard checkout duration
        issue.setStatus(IssueStatus.ISSUED);

        Issue updatedIssue = issueRepository.save(issue);
        return IssueMapper.toDto(updatedIssue);
    }

    @Override
    public void rejectBorrowRequest(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow request not found with id: " + issueId));

        if (issue.getStatus() != IssueStatus.REQUESTED) {
            throw new BadRequestException("Only pending borrow requests can be rejected.");
        }

        issueRepository.delete(issue);
    }

    @Override
    public IssueDto requestReturn(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Checkout record not found with id: " + issueId));

        if (issue.getStatus() != IssueStatus.ISSUED && issue.getStatus() != IssueStatus.OVERDUE) {
            throw new BadRequestException("Only checked out books can be returned. Current status: " + issue.getStatus());
        }

        issue.setStatus(IssueStatus.RETURN_REQUESTED);
        Issue savedIssue = issueRepository.save(issue);
        return IssueMapper.toDto(savedIssue);
    }

    @Override
    public IssueDto approveReturn(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Checkout record not found with id: " + issueId));

        if (issue.getStatus() != IssueStatus.RETURN_REQUESTED && 
            issue.getStatus() != IssueStatus.ISSUED && 
            issue.getStatus() != IssueStatus.OVERDUE) {
            throw new BadRequestException("Cannot process return. Current status: " + issue.getStatus());
        }

        LocalDate returnDate = LocalDate.now();
        issue.setReturnDate(returnDate);

        // Fine and late day calculation
        if (returnDate.isAfter(issue.getDueDate())) {
            long lateDays = ChronoUnit.DAYS.between(issue.getDueDate(), returnDate);
            issue.setLateDays((int) lateDays);
            issue.setFine(lateDays * fineRate);
        } else {
            issue.setLateDays(0);
            issue.setFine(0.0);
        }

        issue.setStatus(IssueStatus.RETURNED);
        Issue savedIssue = issueRepository.save(issue);

        // Re-inject book stock
        Book book = issue.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        book.updateStatusBasedOnAvailability();
        bookRepository.save(book);

        return IssueMapper.toDto(savedIssue);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IssueDto> findFiltered(String search, IssueStatus status, Pageable pageable) {
        return issueRepository.findIssuesFiltered(search, status, pageable)
                .map(IssueMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IssueDto> findMemberHistory(Long memberId, Pageable pageable) {
        return issueRepository.findByMemberId(memberId, pageable)
                .map(IssueMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueDto> findMemberActiveBorrows(Long memberId) {
        return issueRepository.findByMemberId(memberId).stream()
                .filter(i -> i.getStatus() == IssueStatus.ISSUED || 
                             i.getStatus() == IssueStatus.OVERDUE || 
                             i.getStatus() == IssueStatus.RETURN_REQUESTED)
                .map(IssueMapper::toDto)
                .toList();
    }

    @Override
    public void updateOverdueStatusAndFines() {
        LocalDate today = LocalDate.now();
        
        // 1. Process issues that are still ISSUED but past their due date
        List<Issue> overdueIssues = issueRepository.findByStatusAndDueDateBefore(IssueStatus.ISSUED, today);
        for (Issue issue : overdueIssues) {
            issue.setStatus(IssueStatus.OVERDUE);
            long lateDays = ChronoUnit.DAYS.between(issue.getDueDate(), today);
            issue.setLateDays((int) lateDays);
            issue.setFine(lateDays * fineRate);
            issueRepository.save(issue);
        }

        // 2. Refresh fines for issues already marked as OVERDUE
        List<Issue> currentOverdue = issueRepository.findByStatusAndDueDateBefore(IssueStatus.OVERDUE, today);
        for (Issue issue : currentOverdue) {
            long lateDays = ChronoUnit.DAYS.between(issue.getDueDate(), today);
            issue.setLateDays((int) lateDays);
            issue.setFine(lateDays * fineRate);
            issueRepository.save(issue);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveBorrows() {
        return issueRepository.countByStatus(IssueStatus.ISSUED) + issueRepository.countByStatus(IssueStatus.OVERDUE);
    }

    @Override
    @Transactional(readOnly = true)
    public long countOverdueBooks() {
        return issueRepository.countByStatus(IssueStatus.OVERDUE);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalFineCollected() {
        Double total = issueRepository.sumTotalFineCollected();
        return total != null ? total : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Double getMemberPendingFine(Long memberId) {
        Double pending = issueRepository.sumPendingFineByMemberId(memberId);
        return pending != null ? pending : 0.0;
    }
}
