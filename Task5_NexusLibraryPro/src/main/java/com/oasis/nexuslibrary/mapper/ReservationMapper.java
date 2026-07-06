package com.oasis.nexuslibrary.mapper;

import com.oasis.nexuslibrary.dto.ReservationDto;
import com.oasis.nexuslibrary.entity.Reservation;

public class ReservationMapper {

    public static ReservationDto toDto(Reservation reservation) {
        if (reservation == null) return null;
        return new ReservationDto(
                reservation.getId(),
                reservation.getBook() != null ? reservation.getBook().getId() : null,
                reservation.getBook() != null ? reservation.getBook().getTitle() : null,
                reservation.getBook() != null ? reservation.getBook().getIsbn() : null,
                reservation.getMember() != null ? reservation.getMember().getId() : null,
                reservation.getMember() != null ? reservation.getMember().getName() : null,
                reservation.getMember() != null ? reservation.getMember().getEmail() : null,
                reservation.getReservationDate(),
                reservation.getStatus()
        );
    }
}
