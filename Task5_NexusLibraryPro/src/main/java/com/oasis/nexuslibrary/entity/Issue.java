package com.oasis.nexuslibrary.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(nullable = false)
    private Double fine = 0.0;

    @Column(name = "late_days", nullable = false)
    private Integer lateDays = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status;

    // Constructors
    public Issue() {
    }

    public Issue(Long id, Book book, Member member, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate, Double fine, Integer lateDays, IssueStatus status) {
        this.id = id;
        this.book = book;
        this.member = member;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine != null ? fine : 0.0;
        this.lateDays = lateDays != null ? lateDays : 0;
        this.status = status;
    }

    // Builder
    public static IssueBuilder builder() {
        return new IssueBuilder();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Double getFine() {
        return fine;
    }

    public void setFine(Double fine) {
        this.fine = fine;
    }

    public Integer getLateDays() {
        return lateDays;
    }

    public void setLateDays(Integer lateDays) {
        this.lateDays = lateDays;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    // Builder Class
    public static class IssueBuilder {
        private Long id;
        private Book book;
        private Member member;
        private LocalDate issueDate;
        private LocalDate dueDate;
        private LocalDate returnDate;
        private Double fine = 0.0;
        private Integer lateDays = 0;
        private IssueStatus status;

        public IssueBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public IssueBuilder book(Book book) {
            this.book = book;
            return this;
        }

        public IssueBuilder member(Member member) {
            this.member = member;
            return this;
        }

        public IssueBuilder issueDate(LocalDate issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        public IssueBuilder dueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public IssueBuilder returnDate(LocalDate returnDate) {
            this.returnDate = returnDate;
            return this;
        }

        public IssueBuilder fine(Double fine) {
            this.fine = fine;
            return this;
        }

        public IssueBuilder lateDays(Integer lateDays) {
            this.lateDays = lateDays;
            return this;
        }

        public IssueBuilder status(IssueStatus status) {
            this.status = status;
            return this;
        }

        public Issue build() {
            return new Issue(id, book, member, issueDate, dueDate, returnDate, fine, lateDays, status);
        }
    }
}
