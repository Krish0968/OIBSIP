package com.oasis.nexuslibrary.service;

import com.oasis.nexuslibrary.dto.ReservationDto;
import com.oasis.nexuslibrary.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ReservationService {
    ReservationDto createReservation(Long memberId, Long bookId);
    ReservationDto approveReservation(Long reservationId);
    void cancelReservation(Long reservationId);
    void fulfillReservation(Long reservationId);
    
    Page<ReservationDto> findFiltered(String search, ReservationStatus status, Pageable pageable);
    Page<ReservationDto> findMemberReservations(Long memberId, Pageable pageable);
    List<ReservationDto> findMemberActiveReservations(Long memberId);
    
    void checkExpiredReservations();
    
    long countActiveReservations();
}
