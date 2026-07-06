package com.oasis.nexuslibrary.service;

import com.oasis.nexuslibrary.dto.IssueDto;
import com.oasis.nexuslibrary.entity.IssueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IssueService {
    IssueDto requestBorrow(Long memberId, Long bookId);
    IssueDto approveBorrow(Long issueId);
    void rejectBorrowRequest(Long issueId);
    IssueDto requestReturn(Long issueId);
    IssueDto approveReturn(Long issueId);
    
    Page<IssueDto> findFiltered(String search, IssueStatus status, Pageable pageable);
    Page<IssueDto> findMemberHistory(Long memberId, Pageable pageable);
    List<IssueDto> findMemberActiveBorrows(Long memberId);
    
    void updateOverdueStatusAndFines();
    
    long countActiveBorrows();
    long countOverdueBooks();
    Double getTotalFineCollected();
    Double getMemberPendingFine(Long memberId);
}
