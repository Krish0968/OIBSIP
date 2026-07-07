# NexusLibrary Pro - Live Demonstration Walkthrough Script

This script provides a step-by-step guide to demonstrating the core capabilities of **NexusLibrary Pro** for evaluators and presentation purposes.

---

## 🎬 Act 1: Initial Launch & Database Setup

1. **Start the Application**:
   Open a terminal and execute:
   ```bash
   mvn spring-boot:run
   ```
2. **First Boot Check**:
   Observe the terminal console logs. The Hibernate DDL auto engine generates the SQLite tables automatically, and `DataSeeder` inserts the demo data.
3. **Open browser**:
   Navigate to `http://localhost:8080`. Renders a premium, glassmorphic login screen.

---

## 🎭 Act 2: Member Portal Walkthrough (Borrow Request)

1. **Log in as a Library Reader**:
   - Username: `krish@mail.com`
   - Password: `password123`
2. **Explore Reader Dashboard**:
   - The reader sees stats cards showing **0 Books Borrowed**, **0 Active Reservations**, and **₹0.00 Pending Fines**.
   - The recent activity grids are empty.
3. **Browse the Catalog**:
   - Click **Browse Catalog** in the sidebar.
   - Use the category sidebar to filter by **Technology**. The card grid filters immediately.
   - Type "Clean Code" in the search box. The grid updates to show only *Clean Code* and *The Clean Coder*.
4. **Inspect Book Details & Borrow**:
   - Click **Details** on the *Clean Code* card.
   - Note the book details display, shelf code `T-01`, and the availability status card showing **5 of 5 copies available**.
   - Click the gradient button: **Request Book Borrow**.
   - A Bootstrap toast pops up: *"Borrow request submitted successfully! Pending administrator approval."* The button changes to disabled: *"Borrow Request Pending Approval"*.
5. **Log Out**:
   - Click **Logout** at the bottom of the sidebar.

---

## 🎭 Act 3: Admin Portal Walkthrough (Approvals & Sync)

1. **Log in as Library Admin**:
   - Username: `admin`
   - Password: `admin123`
2. **Review Admin Dashboard**:
   - The admin sees the stats counters updated.
   - The Chart.js graph displays checkout/return curves.
   - A blue informational banner appears at the top: *"There is 1 pending book borrow request."* Click the link: **Review Requests**.
3. **Approve Checkout**:
   - In the Requests directory, locate Krish Sharma's request for *Clean Code*.
   - Click **Approve**.
   - A success toast is displayed. The transaction status changes to `ISSUED`, setting the checkout date to today and due date to 14 days later.
4. **Verify Stock Decrease**:
   - Navigate to **Manage Books** in the sidebar.
   - Search "Clean Code".
   - Note that the stock count has dropped to **4 / 5 copies**. The system holds the count correctly.

---

## 🎭 Act 4: Simulating Fines & Return Approvals

1. **Simulate Overdue Checkouts**:
   - To simulate a fine, the administrator can manually trigger synchronization. (In standard use, the background scheduler runs this).
   - Let's run a sync job: Go to Admin Dashboard -> Click **Run Sync Jobs**.
   - Success toast pops up confirming system synchronization.
2. **Log Back in as Member & Request Return**:
   - Logout from Admin, login as `krish@mail.com` / `password123`.
   - Go to **Borrow History**.
   - Locate the active checkout for *Clean Code*. The status is `ISSUED`.
   - Click the orange **Return** button.
   - Toast pops up: *"Return request submitted! Please return the book physical copy to the library desk."* The status updates to `RETURN REQUESTED`.
   - Logout from member.
3. **Accept Return & Collect Fine**:
   - Login back as `admin` / `admin123`.
   - Go to **Borrow Requests** inbox.
   - Locate Krish Sharma's return request. Click **Accept Return**.
   - Return is finalized. Stock for *Clean Code* resets back to **5 / 5 copies**.

---

## 🎭 Act 5: System Reporting & Audit Logs

1. **Navigate to Systems Reports**:
   - Click **System Reports** in the admin sidebar.
   - An overview card compiles database details.
2. **Export Database CSV**:
   - Click **Download CSV** on the *Books Catalog* block. Saves a spreadsheet containing ISBNs, publishers, and shelf details.
3. **Export Stylized PDF Summary**:
   - Click **Download PDF** on the *PDF System Sheet* block.
   - Open the downloaded PDF document. Note the professional layout header, generation timestamp, books table, active checkouts table, and color themes.
