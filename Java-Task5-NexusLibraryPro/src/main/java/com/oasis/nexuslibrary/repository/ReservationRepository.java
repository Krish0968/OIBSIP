package com.oasis.nexuslibrary.repository;

import com.oasis.nexuslibrary.entity.Reservation;
import com.oasis.nexuslibrary.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    long countByStatus(ReservationStatus status);
    
    long countByMemberIdAndStatus(Long memberId, ReservationStatus status);
    
    List<Reservation> findByBookIdAndStatus(Long bookId, ReservationStatus status);
    
    List<Reservation> findByMemberId(Long memberId);
    
    Page<Reservation> findByMemberId(Long memberId, Pageable pageable);
    
    List<Reservation> findByStatusAndReservationDateBefore(ReservationStatus status, LocalDateTime dateTime);

    @Query("SELECT r FROM Reservation r WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(r.member.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(r.book.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(r.book.isbn) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR r.status = :status)")
    Page<Reservation> findReservationsFiltered(@Param("search") String search, 
                                               @Param("status") ReservationStatus status, 
                                               Pageable pageable);
}
