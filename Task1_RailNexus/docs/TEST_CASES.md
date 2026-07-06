# Manual Test Cases: RailNexus

This document contains manual test cases to verify the workflows of **RailNexus** (Online Reservation System).

---

## 1. Authentication & Security

| Test ID | Feature | Preconditions | Test Steps | Expected Result | Actual Result |
|---|---|---|---|---|---|
| **TC-AUTH-01** | Valid Login | Database has seeded user credentials | 1. Enter username `krish`<br>2. Enter password `demo123`<br>3. Click "Login" button | Successfully logs in and opens the Dashboard. Shows welcome message "Welcome back, Krish Sharma". | [Pending] |
| **TC-AUTH-02** | Invalid Username | Username does not exist in DB | 1. Enter username `invalid`<br>2. Enter password `demo123`<br>3. Click "Login" button | Login fails. Shows warning message "Invalid username or password." Clear password field. | [Pending] |
| **TC-AUTH-03** | Invalid Password | Correct username, incorrect password | 1. Enter username `krish`<br>2. Enter password `wrongpass`<br>3. Click "Login" button | Login fails. Shows warning message "Invalid username or password." Clear password field. | [Pending] |
| **TC-AUTH-04** | Empty Credentials | Login form is displayed | 1. Leave fields empty<br>2. Click "Login" button | UI blocks submission. Shows warning "Username is required." Focus returns to empty field. | [Pending] |
| **TC-AUTH-05** | Show/Hide Password | Password field has text | 1. Enter password `demo123`<br>2. Click "Show Password" checkbox | Echo character changes from dots (`•`) to plain text `demo123`. Unchecking toggles it back. | [Pending] |
| **TC-AUTH-06** | Clear Form Button | Username and Password fields have text | 1. Fill fields with dummy text<br>2. Click "Clear" button | Both input fields are reset to empty. Error labels are cleared. | [Pending] |

---

## 2. Train Booking & Validation

| Test ID | Feature | Preconditions | Test Steps | Expected Result | Actual Result |
|---|---|---|---|---|---|
| **TC-BOOK-01** | Train Auto-Population | Logged in to dashboard | 1. Go to "Book Ticket" tab<br>2. Select train number `1001` from combobox | Train Name, Source Station, Destination Station, Dep Time, and Arr Time are automatically filled and are read-only. | [Pending] |
| **TC-BOOK-02** | Empty Passenger Name | Form is displayed | 1. Select train and date<br>2. Leave Passenger Name blank<br>3. Click "Book Ticket" button | Booking fails. Warning dialog shows "Passenger name cannot be empty." Focus returned. | [Pending] |
| **TC-BOOK-03** | Past Journey Date | Form is displayed | 1. Fill passenger name<br>2. Set Journey Date spinner to yesterday<br>3. Click "Book Ticket" button | Booking fails. Warning dialog shows "Journey date cannot be in the past." | [Pending] |
| **TC-BOOK-04** | Same Stations Validation | Fictional train check | 1. Validate that seeded train stations do not match | System prevents booking if source and destination stations are identical. | [Pending] |
| **TC-BOOK-05** | Successful Booking | Valid passenger details entered | 1. Fill passenger name `Jane Doe`<br>2. Select train `1002`<br>3. Select class `AC 3 Tier`<br>4. Click "Book Ticket"<br>5. Review details in dialog and click "OK" | Shows booking success dialog. Displays unique PNR. Option to "Book Another" or "View My Bookings" appears. | [Pending] |

---

## 3. Booking Management & Filters

| Test ID | Feature | Preconditions | Test Steps | Expected Result | Actual Result |
|---|---|---|---|---|---|
| **TC-MGMT-01** | My Bookings Table | Successful booking exists in database | 1. Click "My Bookings" tab | JTable lists user's bookings showing correct PNR, passenger name, train name, date, and CONFIRMED status. | [Pending] |
| **TC-MGMT-02** | Dynamic Search Bar | Multiple bookings exist | 1. Type passenger name or PNR in search field | Table rows filter in real-time matching the input query. | [Pending] |
| **TC-MGMT-03** | Status Filter Dropdown | Confirmed and cancelled bookings exist | 1. Toggle combobox between "All", "Confirmed", and "Cancelled" | Table displays only rows matching the selected filter. | [Pending] |
| **TC-MGMT-04** | Double Click Details | Table has rows | 1. Double click a booking row | Modal popup opens showing complete booking details (Timings, PNR, booked timestamp). | [Pending] |

---

## 4. Cancellation & Security Boundaries

| Test ID | Feature | Preconditions | Test Steps | Expected Result | Actual Result |
|---|---|---|---|---|---|
| **TC-CANCEL-01**| Successful PNR Lookup | Valid booking exists | 1. Go to "Cancel Reservation"<br>2. Enter PNR of active ticket<br>3. Click "Search / Fetch" | Displays full, correct booking details. Status reads "CONFIRMED". "Confirm Cancellation" button is enabled. | [Pending] |
| **TC-CANCEL-02**| Cancel Reservation | Booking is retrieved | 1. Click "Confirm Cancellation"<br>2. Select "YES" in "Are you sure?" warning dialog | Success alert shows. Status changes to "CANCELLED". Cancellation timestamp is set. Button is disabled. | [Pending] |
| **TC-CANCEL-03**| Duplicate Cancellation | Ticket is cancelled | 1. Fetch the cancelled PNR again | Status reads "CANCELLED". The "Confirm Cancellation" button is disabled to prevent duplicate cancel actions. | [Pending] |
| **TC-CANCEL-04**| Invalid PNR Lookup | Booking panel is open | 1. Enter random PNR code<br>2. Click search | Warning dialog alerts "No reservation found for PNR: [Input]". Details panel remains hidden. | [Pending] |
| **TC-CANCEL-05**| Session Boundary Check | Multiple users registered | 1. Log in as `passenger1`<br>2. Search for the PNR created by user `krish` | System alerts "Access Denied: You are not authorized to view this booking." details panel remains hidden. | [Pending] |

---

## 5. Persistence & State Transitions

| Test ID | Feature | Preconditions | Test Steps | Expected Result | Actual Result |
|---|---|---|---|---|---|
| **TC-STATE-01** | Data Persistence | Booking & cancellations made | 1. Close application window<br>2. Relaunch project<br>3. Log in with same account | All previous bookings, status changes, and cancellation details remain correctly restored from SQLite DB. | [Pending] |
| **TC-STATE-02** | Logout/Switch Account | User logged in | 1. Click "Logout" in header<br>2. Select "YES"<br>3. Log in as a different user | Clears session. Displays login screen. Logs in new user and displays only their respective bookings. | [Pending] |
