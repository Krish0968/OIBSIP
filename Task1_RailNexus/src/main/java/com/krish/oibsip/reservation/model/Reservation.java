package com.krish.oibsip.reservation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private String pnr;
    private int userId;
    private String passengerName;
    private int trainId;
    private String classType;
    private LocalDate journeyDate;
    private String sourceStation;
    private String destinationStation;
    private String bookingStatus; // CONFIRMED or CANCELLED
    private LocalDateTime bookedAt;
    private LocalDateTime cancelledAt;

    // Helper properties for display (loaded via SQL join)
    private int trainNumber;
    private String trainName;

    public Reservation() {}

    public Reservation(int id, String pnr, int userId, String passengerName, int trainId, String classType, 
                       LocalDate journeyDate, String sourceStation, String destinationStation, 
                       String bookingStatus, LocalDateTime bookedAt, LocalDateTime cancelledAt) {
        this.id = id;
        this.pnr = pnr;
        this.userId = userId;
        this.passengerName = passengerName;
        this.trainId = trainId;
        this.classType = classType;
        this.journeyDate = journeyDate;
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
        this.bookingStatus = bookingStatus;
        this.bookedAt = bookedAt;
        this.cancelledAt = cancelledAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public int getTrainId() {
        return trainId;
    }

    public void setTrainId(int trainId) {
        this.trainId = trainId;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public String getSourceStation() {
        return sourceStation;
    }

    public void setSourceStation(String sourceStation) {
        this.sourceStation = sourceStation;
    }

    public String getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(String destinationStation) {
        this.destinationStation = destinationStation;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public int getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(int trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }
}
