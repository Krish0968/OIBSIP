package com.oasis.nexuslibrary.service.impl;

import com.oasis.nexuslibrary.dto.IssueDto;
import com.oasis.nexuslibrary.dto.ReservationDto;
import com.oasis.nexuslibrary.entity.IssueStatus;
import com.oasis.nexuslibrary.entity.ReservationStatus;
import com.oasis.nexuslibrary.entity.Role;
import com.oasis.nexuslibrary.mapper.IssueMapper;
import com.oasis.nexuslibrary.mapper.ReservationMapper;
import com.oasis.nexuslibrary.repository.BookRepository;
import com.oasis.nexuslibrary.repository.IssueRepository;
import com.oasis.nexuslibrary.repository.MemberRepository;
import com.oasis.nexuslibrary.repository.ReservationRepository;
import com.oasis.nexuslibrary.service.ReportService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final IssueRepository issueRepository;
    private final ReservationRepository reservationRepository;

    public ReportServiceImpl(BookRepository bookRepository, MemberRepository memberRepository, 
                             IssueRepository issueRepository, ReservationRepository reservationRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.issueRepository = issueRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Map<String, Object> getAdminDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalBooks = bookRepository.count();
        long totalMembers = memberRepository.countByRole(Role.MEMBER);
        long booksIssued = issueRepository.countByStatus(IssueStatus.ISSUED);
        long overdueBooks = issueRepository.countByStatus(IssueStatus.OVERDUE);
        long activeReservations = reservationRepository.countByStatus(ReservationStatus.PENDING) 
                + reservationRepository.countByStatus(ReservationStatus.APPROVED);
        
        Double totalFineCollected = issueRepository.sumTotalFineCollected();
        if (totalFineCollected == null) totalFineCollected = 0.0;

        stats.put("totalBooks", totalBooks);
        stats.put("totalMembers", totalMembers);
        stats.put("booksIssued", booksIssued);
        stats.put("overdueBooks", overdueBooks);
        stats.put("activeReservations", activeReservations);
        stats.put("totalFineCollected", totalFineCollected);

        // Generate last 6 months statistics for Chart.js
        List<String> chartLabels = new ArrayList<>();
        List<Long> checkoutData = new ArrayList<>();
        List<Long> returnData = new ArrayList<>();

        LocalDate today = LocalDate.now();
        DateTimeFormatter queryFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("MMM yy");

        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = today.minusMonths(i);
            String yearMonthStr = monthDate.format(queryFormatter);
            String labelStr = monthDate.format(labelFormatter);

            chartLabels.add(labelStr);
            checkoutData.add(issueRepository.countIssuesByMonth(yearMonthStr));
            returnData.add(issueRepository.countReturnsByMonth(yearMonthStr));
        }

        stats.put("chartLabels", chartLabels);
        stats.put("checkoutData", checkoutData);
        stats.put("returnData", returnData);

        return stats;
    }

    @Override
    public Map<String, Object> getMemberDashboardStats(Long memberId) {
        Map<String, Object> stats = new HashMap<>();

        long borrowedBooks = issueRepository.countByMemberIdAndStatus(memberId, IssueStatus.ISSUED) 
                + issueRepository.countByMemberIdAndStatus(memberId, IssueStatus.OVERDUE);

        long activeReservations = reservationRepository.countByMemberIdAndStatus(memberId, ReservationStatus.PENDING)
                + reservationRepository.countByMemberIdAndStatus(memberId, ReservationStatus.APPROVED);

        Double pendingFine = issueRepository.sumPendingFineByMemberId(memberId);
        if (pendingFine == null) pendingFine = 0.0;

        stats.put("borrowedBooks", borrowedBooks);
        stats.put("activeReservations", activeReservations);
        stats.put("pendingFine", pendingFine);

        // Recent activity feed: last 5 borrow requests/checkouts
        PageRequest borrowPage = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));
        List<IssueDto> recentBorrows = issueRepository.findByMemberId(memberId, borrowPage)
                .map(IssueMapper::toDto)
                .getContent();

        // Last 5 reservations
        PageRequest reservePage = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));
        List<ReservationDto> recentReservations = reservationRepository.findByMemberId(memberId, reservePage)
                .map(ReservationMapper::toDto)
                .getContent();

        stats.put("recentBorrows", recentBorrows);
        stats.put("recentReservations", recentReservations);

        return stats;
    }
}
