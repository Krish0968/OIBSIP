# NexusLibrary Pro - Test Suite & Verification Cases

This document describes the test cases designed to verify the correctness, reliability, security, and integrity of the NexusLibrary Pro system.

---

## 🧪 Automated Unit & Mock Tests

Automated testing is configured under `src/test/java/` using JUnit 5 and Mockito.

### Execution Commands
To run the automated tests, open a terminal in the project directory and execute:
```bash
mvn test
```

### Automated Test Cases Overview
1. **`testBorrowRequestSubmitsSuccessfully`**: Verifies that a reader can request a book. Asserts that the request status is `REQUESTED` and no stock is drop-decreased yet.
2. **`testApproveBorrowDecreasesStockAndStartsCheckout`**: Verifies that the administrator's approval decrements available book stock, sets the issue date to today, sets the due date to 14 days later, and changes the transaction status to `ISSUED`.
3. **`testApproveReturnCalculatesFineCorrectly`**: Verifies that when returning an overdue book, the return date is saved, the late days are calculated correctly, a fine of ₹10.00 per day is applied, and the book stock is incremented.
4. **`testApproveReservationHoldsBookCopies`**: Verifies that approving a reader hold reserves a copy, decrementing the available count.

---

## 📋 Manual Verification Checklist

Below is a detailed walkthrough of manual QA test cases that should be executed to verify full system features.

### 1. Authentication & Role Authorization

| Test Case ID | Description | Input / Action | Expected Result |
| :--- | :--- | :--- | :--- |
| **AUTH-01** | Standard Admin Login | Username: `admin`<br>Password: `admin123` | Redirects to `/admin/dashboard`. Admin sidebar is visible. |
| **AUTH-02** | Standard Member Login | Username: `krish@mail.com`<br>Password: `password123` | Redirects to `/member/dashboard`. Member sidebar is visible. |
| **AUTH-03** | Invalid Login Credentials | Username: `admin`<br>Password: `wrong_pass` | Remains on login page. Shows error alert: "Invalid email or password." |
| **AUTH-04** | Unauthorized Access Attempt | Accessing `/admin/dashboard` while logged in as member | Renders custom `error/403` Access Denied page. |
| **AUTH-05** | Reader Registration | Fill registration form with valid email | Creates account, hashes password, redirects to login page showing success toast. |
| **AUTH-06** | Duplicate Email Registration | Register with `krish@mail.com` | Registration fails, displays error: "Email address is already registered." |

### 2. Catalog & Details Panel

| Test Case ID | Description | Input / Action | Expected Result |
| :--- | :--- | :--- | :--- |
| **CAT-01** | Search Filter | Input "Clean Code" in catalog search | Only "Clean Code" book card is displayed in the list. |
| **CAT-02** | Genre Filter Sidebar | Click "Sci-Fi" sidebar link | Displays only science fiction genre books. |
| **CAT-03** | Book Details Sheet | Click "Details" on "Clean Code" card | Renders description, ISBN, publisher details, available copies count, and borrow button. |

### 3. Borrowing & Fine System Workflow

| Test Case ID | Description | Input / Action | Expected Result |
| :--- | :--- | :--- | :--- |
| **BOR-01** | Borrow Request | Click "Request Book Borrow" on details page | Request is submitted, button changes to disabled: "Borrow Request Pending Approval". |
| **BOR-02** | Admin Approve Borrow | Go to Admin Portal -> Requests -> Click "Approve" | Request changes to `ISSUED`. Book available copies drop by 1. |
| **BOR-03** | Max Borrow Limit Check | Attempt to request a 6th book borrow | Request is blocked by system: "You have reached the maximum borrow limit...". |
| **BOR-04** | Return Request | Member History -> Click "Return" | Status changes to `RETURN_REQUESTED`. Button displays pending message. |
| **BOR-05** | Admin Approve Return | Go to Admin Portal -> Requests -> Click "Accept Return" | Status changes to `RETURNED`. Available copies increment by 1. |
| **BOR-06** | Overdue Fine Calculation | Let a book due date pass, click "Run Sync Jobs" | Status changes to `OVERDUE`. Fine calculated at ₹10/day. |
| **BOR-07** | Account Suspension Lock | Admin suspends member. Member tries to borrow. | Member receives error toast: "Your account is suspended. You cannot borrow books." |

### 4. Book Reservations

| Test Case ID | Description | Input / Action | Expected Result |
| :--- | :--- | :--- | :--- |
| **RES-01** | Reserve Out of Stock Book | Book available copies = 0. Click "Reserve" | Reservation created in `PENDING` status. |
| **RES-02** | Approve Reservation Hold | Admin clicks "Hold Copy" | Reservation status becomes `APPROVED`. Available copies drop by 1. |
| **RES-03** | Fulfill Reservation | Admin clicks "Fulfill Issue" | Reservation changes to `COMPLETED`. A new active checkout (`ISSUED`) is created. |
| **RES-04** | Hold Expiry Check | approved reservation older than 3 days. Run Sync Jobs. | Reservation status becomes `CANCELLED`. Book available copies increment by 1. |

### 5. Catalog CRUD (Admin Only)

| Test Case ID | Description | Input / Action | Expected Result |
| :--- | :--- | :--- | :--- |
| **CRUD-01** | Add Book | Fill Book form with valid details | Book added to catalog. Visible in admin list and member catalog. |
| **CRUD-02** | Duplicate ISBN | Try to add book with existing ISBN | Save fails, shows error: "Book with ISBN already exists." |
| **CRUD-03** | Add Category / Author | Fill inline add form in respective sheets | Entry created, available in book-form select dropdowns. |
| **CRUD-04** | Delete Active Book | Try to delete book that is currently checked out | Deletion is blocked: "Cannot delete book as some copies are currently issued." |

### 6. Exports & Audits

| Test Case ID | Description | Input / Action | Expected Result |
| :--- | :--- | :--- | :--- |
| **EXP-01** | PDF Export | Admin Reports -> Click "Download PDF" | A formatted PDF report is downloaded. |
| **EXP-02** | CSV Export | Admin Reports -> Click "Download CSV" on Books | A CSV spreadsheet containing all catalog books is downloaded. |
