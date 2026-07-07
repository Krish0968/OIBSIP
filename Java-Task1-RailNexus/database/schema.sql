-- RailNexus Database Schema
PRAGMA foreign_keys = ON;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    full_name TEXT NOT NULL,
    created_at TEXT NOT NULL
);

-- Trains Table
CREATE TABLE IF NOT EXISTS trains (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    train_number INTEGER UNIQUE NOT NULL,
    train_name TEXT NOT NULL,
    source_station TEXT NOT NULL,
    destination_station TEXT NOT NULL,
    departure_time TEXT NOT NULL,
    arrival_time TEXT NOT NULL
);

-- Reservations Table (with soft cancellation support)
CREATE TABLE IF NOT EXISTS reservations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    pnr TEXT UNIQUE NOT NULL,
    user_id INTEGER NOT NULL,
    passenger_name TEXT NOT NULL,
    train_id INTEGER NOT NULL,
    class_type TEXT NOT NULL,
    journey_date TEXT NOT NULL,
    source_station TEXT NOT NULL,
    destination_station TEXT NOT NULL,
    booking_status TEXT NOT NULL DEFAULT 'CONFIRMED', -- CONFIRMED or CANCELLED
    booked_at TEXT NOT NULL,
    cancelled_at TEXT,
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(train_id) REFERENCES trains(id)
);

-- Indexes for performance optimization
CREATE INDEX IF NOT EXISTS idx_reservations_pnr ON reservations(pnr);
CREATE INDEX IF NOT EXISTS idx_reservations_user_id ON reservations(user_id);
CREATE INDEX IF NOT EXISTS idx_trains_train_number ON trains(train_number);
