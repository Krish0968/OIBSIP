package com.oasis.nexuslibrary.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private String language;

    @Column(name = "publication_year", nullable = false)
    private Integer publicationYear;

    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies;

    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies;

    @Column(name = "shelf_number")
    private String shelfNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status;



    @Column(length = 2000)
    private String description;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Issue> issues = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
        if (availableCopies == null) {
            availableCopies = totalCopies;
        }
        updateStatusBasedOnAvailability();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
        updateStatusBasedOnAvailability();
    }

    public void updateStatusBasedOnAvailability() {
        if (availableCopies <= 0) {
            status = BookStatus.OUT_OF_STOCK;
        } else {
            status = BookStatus.AVAILABLE;
        }
    }

    // Constructors
    public Book() {
    }

    public Book(Long id, String isbn, String title, Author author, Category category, String publisher, String language,
                Integer publicationYear, Integer totalCopies, Integer availableCopies, String shelfNumber, BookStatus status,
                String description) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.publisher = publisher;
        this.language = language;
        this.publicationYear = publicationYear;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.shelfNumber = shelfNumber;
        this.status = status;
        this.description = description;
    }

    // Builder
    public static BookBuilder builder() {
        return new BookBuilder();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Integer getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(Integer totalCopies) {
        this.totalCopies = totalCopies;
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }

    public String getShelfNumber() {
        return shelfNumber;
    }

    public void setShelfNumber(String shelfNumber) {
        this.shelfNumber = shelfNumber;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    // Builder Class
    public static class BookBuilder {
        private Long id;
        private String isbn;
        private String title;
        private Author author;
        private Category category;
        private String publisher;
        private String language;
        private Integer publicationYear;
        private Integer totalCopies;
        private Integer availableCopies;
        private String shelfNumber;
        private BookStatus status;
        private String description;

        public BookBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BookBuilder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public BookBuilder title(String title) {
            this.title = title;
            return this;
        }

        public BookBuilder author(Author author) {
            this.author = author;
            return this;
        }

        public BookBuilder category(Category category) {
            this.category = category;
            return this;
        }

        public BookBuilder publisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        public BookBuilder language(String language) {
            this.language = language;
            return this;
        }

        public BookBuilder publicationYear(Integer publicationYear) {
            this.publicationYear = publicationYear;
            return this;
        }

        public BookBuilder totalCopies(Integer totalCopies) {
            this.totalCopies = totalCopies;
            return this;
        }

        public BookBuilder availableCopies(Integer availableCopies) {
            this.availableCopies = availableCopies;
            return this;
        }

        public BookBuilder shelfNumber(String shelfNumber) {
            this.shelfNumber = shelfNumber;
            return this;
        }

        public BookBuilder status(BookStatus status) {
            this.status = status;
            return this;
        }



        public BookBuilder description(String description) {
            this.description = description;
            return this;
        }

        public Book build() {
            return new Book(id, isbn, title, author, category, publisher, language, publicationYear, totalCopies, availableCopies, shelfNumber, status, description);
        }
    }
}
