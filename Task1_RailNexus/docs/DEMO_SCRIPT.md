# Demo Video Script: RailNexus

This script is designed for a **3 to 5-minute video presentation** of **RailNexus**, the Smart Railway Reservation System.

---

## Static Title Card (First 2-3 Seconds)
The video must start with a static slide showing:
```
[YOUR FULL NAME]
Java Development Internship
Task 1 — Online Reservation System
```
*Direct Narration*: "(Silent pause for 2 seconds) Hello everyone! My name is [Your Name], and I am a Java Development Intern at Oasis Infobyte. Today, I am going to demonstrate my project for Task 1: RailNexus, a Smart Railway Reservation System built using Java Swing and SQLite."

---

## Scene 1: Login Screen & Validation (0:03 - 0:45)
- **Visuals**: Show the FlatLaf dark-themed login screen. Point out the "RAILNEXUS" branding and the "Demo Evaluation Credentials" hint box at the bottom.
- **Action**:
  1. Click "Login" with empty fields to trigger validation.
  2. Type `krish` in username, but leave password empty. Click "Login".
  3. Type incorrect password `wrongpass` and click login.
  4. Toggle the "Show Password" checkbox to show it hides/shows the password.
  5. Click "Clear" to reset the fields.
- **Narration**: "Here is our login screen. As you can see, we have the RailNexus branding and a credentials hint section for testers. If I try to submit without entering credentials, the system blocks me. Similarly, entering an invalid password triggers an error. I can also toggle password visibility or clear the form with a single click."

---

## Scene 2: Successful Login & Dashboard (0:45 - 1:20)
- **Visuals**: Type `krish` and `demo123`, then click login. The UI transitions smoothly (after a short loading state) to the Dashboard.
- **Action**: Highlight the welcome message, the three summary cards (Total, Confirmed, Cancelled Bookings), the Recent Bookings table, and the quick actions.
- **Narration**: "Let's log in using our seeded account. The system validates the hash in our local SQLite database. We are now logged in and redirected to the Dashboard. The dashboard welcomes the active user, Krish Sharma, and shows real-time stats cards and a list of our recent bookings. All of this is fetched dynamically from the database."

---

## Scene 3: Booking a Ticket (1:20 - 2:10)
- **Visuals**: Switch to the "Book Ticket" tab.
- **Action**:
  1. Type passenger name `Jane Doe`.
  2. Open the Train Selection combobox. Point out the list of realistic Indian train numbers.
  3. Select train `1002`. Watch all timing and route fields auto-populate instantly as read-only.
  4. Change Journey Date. Try to set it to a past date to trigger a validation warning.
  5. Fix the date, select a class, and click "Book Ticket".
  6. Review details in the summary modal and click "OK" to book.
- **Narration**: "Let's book a ticket. I'll enter the passenger name. Next, we select a train. Choosing a train number dynamically auto-populates the train name, stations, and timings, which are read-only. If I select a past journey date, the application validation flags it. Correcting this date and submitting opens a booking review dialog to confirm the itinerary. Clicking OK saves the booking and generates a unique PNR."

---

## Scene 4: My Bookings & Filtering (2:10 - 2:50)
- **Visuals**: Switch to "My Bookings" tab.
- **Action**:
  1. Point out the newly booked ticket at the top of the table.
  2. Type `Jane` in the search box to filter the table.
  3. Change status filter from "All" to "Cancelled", then "Confirmed".
  4. Double-click the ticket row to open the detailed popup panel.
- **Narration**: "Now we're in the My Bookings section. Our new ticket appears in the list. I can search for bookings by name or PNR, or filter them by status. Double-clicking any row opens a detailed view showing the departure timings and booking timestamps."

---

## Scene 5: Ticket Cancellation (2:50 - 3:30)
- **Visuals**: Switch to "Cancel Reservation" tab.
- **Action**:
  1. Copy the PNR from the dashboard or type it in.
  2. Click "Search / Fetch". Details populate.
  3. Click "Confirm Cancellation". Click "YES" in confirmation.
  4. Details update to show status is now red "CANCELLED" and the cancellation button is disabled.
  5. Go back to Dashboard and My Bookings to show statistics updated.
- **Narration**: "To cancel a booking, we go to the Cancellation screen and search for our PNR. The system fetches the ticket details. Clicking Confirm Cancellation triggers an 'Are you sure?' dialog. Once confirmed, the system performs a soft cancellation, updating the status to Cancelled and setting a cancelled timestamp. Going back to the Dashboard, we can see the cancelled stats counter has incremented."

---

## Scene 6: Security Boundary Check & Logout (3:30 - 4:00)
- **Visuals**:
  1. Log out. Log in as `passenger1` (password: `pass123`).
  2. Go to "Cancel Reservation" tab. Try to search for the PNR we created for user `krish`.
  3. Show the "Access Denied" error message dialog.
  4. Log out again.
- **Narration**: "Security is a core design element of RailNexus. If we log out and sign in as a different user, say passenger1, and try to search or cancel the ticket belonging to krish, the system blocks the access with an Authorization error. This ensures complete privacy."

---

## Scene 7: Codebase structure & Closing (4:00 - 4:30)
- **Visuals**: Open the project folder in your IDE, pointing out the clean structure (`src/main`, `database`, `docs`, `pom.xml`).
- **Action**: Scroll through the files briefly.
- **Narration**: "Finally, here is the clean folder structure of our Maven project. We have separated our concern into UI, Service, DAO, Model, and Util classes, and all database interactions use secure PreparedStatements. Thank you for watching my demo of the RailNexus Reservation System!"
