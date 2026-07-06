package com.oasis.nexuslibrary.repository;

import com.oasis.nexuslibrary.entity.Issue;
import com.oasis.nexuslibrary.entity.IssueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    
    long countByStatus(IssueStatus status);
    
    long countByMemberIdAndStatus(Long memberId, IssueStatus status);
    
    List<Issue> findByMemberId(Long memberId);
    
    Page<Issue> findByMemberId(Long memberId, Pageable pageable);
    
    List<Issue> findByStatusAndDueDateBefore(IssueStatus status, LocalDate date);
    
    @Query("SELECT SUM(i.fine) FROM Issue i WHERE i.fine > 0")
    Double sumTotalFineCollected();

    @Query("SELECT SUM(i.fine) FROM Issue i WHERE i.member.id = :memberId AND i.fine > 0 AND i.status != 'RETURNED'")
    Double sumPendingFineByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT i FROM Issue i WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(i.member.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(i.book.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(i.book.isbn) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR i.status = :status)")
    Page<Issue> findIssuesFiltered(@Param("search") String search, 
                                   @Param("status") IssueStatus status, 
                                   Pageable pageable);

    @Query("SELECT COUNT(i) FROM Issue i WHERE strftime('%Y-%m', i.issueDate) = :yearMonth")
    long countIssuesByMonth(@Param("yearMonth") String yearMonth);

    @Query("SELECT COUNT(i) FROM Issue i WHERE strftime('%Y-%m', i.returnDate) = :yearMonth AND i.status = 'RETURNED'")
    long countReturnsByMonth(@Param("yearMonth") String yearMonth);
}
