package com.oasis.nexuslibrary.controller;

import com.oasis.nexuslibrary.dto.*;
import com.oasis.nexuslibrary.entity.BookStatus;
import com.oasis.nexuslibrary.entity.IssueStatus;
import com.oasis.nexuslibrary.entity.MemberStatus;
import com.oasis.nexuslibrary.entity.ReservationStatus;
import com.oasis.nexuslibrary.entity.Role;
import com.oasis.nexuslibrary.exception.BadRequestException;
import com.oasis.nexuslibrary.service.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final MemberService memberService;
    private final IssueService issueService;
    private final ReservationService reservationService;
    private final ReportService reportService;

    public AdminController(BookService bookService, AuthorService authorService, CategoryService categoryService,
                           MemberService memberService, IssueService issueService, ReservationService reservationService,
                           ReportService reportService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.categoryService = categoryService;
        this.memberService = memberService;
        this.issueService = issueService;
        this.reservationService = reservationService;
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Map<String, Object> stats = reportService.getAdminDashboardStats();
        model.addAllAttributes(stats);
        
        // Add pending borrow requests count and pending reservation requests count for alerts
        model.addAttribute("pendingBorrowsCount", issueService.findFiltered("", IssueStatus.REQUESTED, PageRequest.of(0, 1)).getTotalElements());
        model.addAttribute("pendingReservationsCount", reservationService.findFiltered("", ReservationStatus.PENDING, PageRequest.of(0, 1)).getTotalElements());
        
        return "admin/dashboard";
    }

    // Trigger System Jobs manually (Overdue status recalculation and reservation checks)
    @PostMapping("/system/trigger-jobs")
    public String triggerSystemJobs(RedirectAttributes redirectAttributes) {
        try {
            issueService.updateOverdueStatusAndFines();
            reservationService.checkExpiredReservations();
            redirectAttributes.addFlashAttribute("successToast", "System jobs triggered: Overdue fines updated & expired reservations cancelled.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorToast", "Failed to trigger system jobs: " + ex.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    // ==========================================
    // BOOK CRUD
    // ==========================================
    @GetMapping("/books")
    public String listBooks(@RequestParam(value = "search", required = false, defaultValue = "") String search,
                            @RequestParam(value = "categoryId", required = false) Long categoryId,
                            @RequestParam(value = "authorId", required = false) Long authorId,
                            @RequestParam(value = "status", required = false) BookStatus status,
                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                            Model model) {
        Page<BookDto> booksPage = bookService.findFiltered(search, categoryId, authorId, status, PageRequest.of(page, size, Sort.by("id").descending()));
        
        model.addAttribute("books", booksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", booksPage.getTotalPages());
        model.addAttribute("totalItems", booksPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedAuthorId", authorId);
        model.addAttribute("selectedStatus", status);
        
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("statuses", BookStatus.values());
        
        return "admin/books";
    }

    @GetMapping("/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new BookDto());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/book-form";
    }

    @PostMapping("/books/new")
    public String saveBook(@Valid @ModelAttribute("book") BookDto bookDto,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            return "admin/book-form";
        }
        try {
            bookService.save(bookDto);
            redirectAttributes.addFlashAttribute("successToast", "Book cataloged successfully!");
            return "redirect:/admin/books";
        } catch (BadRequestException ex) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("isbnError", ex.getMessage());
            return "admin/book-form";
        }
    }

    @GetMapping("/books/edit/{id}")
    public String editBookForm(@PathVariable("id") Long id, Model model) {
        BookDto bookDto = bookService.findById(id);
        model.addAttribute("book", bookDto);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/book-form";
    }

    @PostMapping("/books/edit/{id}")
    public String updateBook(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("book") BookDto bookDto,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            return "admin/book-form";
        }
        try {
            bookService.update(id, bookDto);
            redirectAttributes.addFlashAttribute("successToast", "Book details updated successfully!");
            return "redirect:/admin/books";
        } catch (BadRequestException ex) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("isbnError", ex.getMessage());
            return "admin/book-form";
        }
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.delete(id);
            redirectAttributes.addFlashAttribute("successToast", "Book deleted from catalog.");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/admin/books";
    }

    // ==========================================
    // AUTHOR CRUD
    // ==========================================
    @GetMapping("/authors")
    public String listAuthors(Model model) {
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("author", new AuthorDto());
        return "admin/authors";
    }

    @PostMapping("/authors/new")
    public String saveAuthor(@Valid @ModelAttribute("author") AuthorDto authorDto,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorToast", "Failed to save author. Name cannot be blank.");
            return "redirect:/admin/authors";
        }
        try {
            authorService.save(authorDto);
            redirectAttributes.addFlashAttribute("successToast", "Author added successfully!");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/admin/authors";
    }

    @PostMapping("/authors/edit/{id}")
    public String updateAuthor(@PathVariable("id") Long id,
                               @Valid @ModelAttribute("author") AuthorDto authorDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorToast", "Failed to update author. Invalid details.");
            return "redirect:/admin/authors";
        }
        try {
            authorService.update(id, authorDto);
            redirectAttributes.addFlashAttribute("successToast", "Author details updated!");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/admin/authors";
    }

    @GetMapping("/authors/delete/{id}")
    public String deleteAuthor(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            authorService.delete(id);
            redirectAttributes.addFlashAttribute("successToast", "Author deleted successfully.");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/admin/authors";
    }

    // ==========================================
    // CATEGORY CRUD
    // ==========================================
    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("category", new CategoryDto());
        return "admin/categories";
    }

    @PostMapping("/categories/new")
    public String saveCategory(@Valid @ModelAttribute("category") CategoryDto categoryDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorToast", "Failed to save category. Name cannot be blank.");
            return "redirect:/admin/categories";
        }
        try {
            categoryService.save(categoryDto);
            redirectAttributes.addFlashAttribute("successToast", "Category created successfully!");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/edit/{id}")
    public String updateCategory(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("category") CategoryDto categoryDto,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorToast", "Failed to update category. Invalid details.");
            return "redirect:/admin/categories";
        }
        try {
            categoryService.update(id, categoryDto);
            redirectAttributes.addFlashAttribute("successToast", "Category updated successfully!");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("successToast", "Category deleted successfully.");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // ==========================================
    // MEMBER MANAGEMENT
    // ==========================================
    @GetMapping("/members")
    public String listMembers(@RequestParam(value = "search", required = false, defaultValue = "") String search,
                              @RequestParam(value = "status", required = false) MemberStatus status,
                              @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                              @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                              Model model) {
        Page<MemberDto> membersPage = memberService.findFiltered(search, Role.MEMBER, status, PageRequest.of(page, size, Sort.by("id").descending()));
        
        model.addAttribute("members", membersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", membersPage.getTotalPages());
        model.addAttribute("totalItems", membersPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", MemberStatus.values());
        
        return "admin/members";
    }

    @PostMapping("/members/toggle-status/{id}")
    public String toggleMemberStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            MemberDto member = memberService.findById(id);
            if (member.getStatus() == MemberStatus.ACTIVE) {
                member.setStatus(MemberStatus.SUSPENDED);
                redirectAttributes.addFlashAttribute("successToast", "Member '" + member.getName() + "' suspended.");
            } else {
                member.setStatus(MemberStatus.ACTIVE);
                redirectAttributes.addFlashAttribute("successToast", "Member '" + member.getName() + "' activated.");
            }
            memberService.update(id, member);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorToast", "Failed to update member status: " + ex.getMessage());
        }
        return "redirect:/admin/members";
    }

    // ==========================================
    // BORROW REQUESTS & RETURNS APPROVALS
    // ==========================================
    @GetMapping("/requests")
    public String listRequests(@RequestParam(value = "search", required = false, defaultValue = "") String search,
                               @RequestParam(value = "status", required = false) IssueStatus status,
                               @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                               @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                               Model model) {
        Page<IssueDto> issuesPage = issueService.findFiltered(search, status, PageRequest.of(page, size, Sort.by("id").descending()));
        
        model.addAttribute("requests", issuesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", issuesPage.getTotalPages());
        model.addAttribute("totalItems", issuesPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", IssueStatus.values());
        
        return "admin/requests";
    }

    @GetMapping("/requests/borrow/approve/{id}")
    public String approveBorrow(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            issueService.approveBorrow(id);
            redirectAttributes.addFlashAttribute("successToast", "Borrow checkout request approved.");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/admin/requests";
    }

    @GetMapping("/requests/borrow/reject/{id}")
    public String rejectBorrow(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            issueService.rejectBorrowRequest(id);
            redirectAttributes.addFlashAttribute("successToast", "Borrow checkout request rejected.");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/admin/requests";
    }

    @GetMapping("/requests/return/approve/{id}")
    public String approveReturn(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            issueService.approveReturn(id);
            redirectAttributes.addFlashAttribute("successToast", "Book return finalized successfully.");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/admin/requests";
    }

    // ==========================================
    // RESERVATIONS
    // ==========================================
    @GetMapping("/reservations")
    public String listReservations(@RequestParam(value = "search", required = false, defaultValue = "") String search,
                                   @RequestParam(value = "status", required = false) ReservationStatus status,
                                   @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                   @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                   Model model) {
        Page<ReservationDto> reservationsPage = reservationService.findFiltered(search, status, PageRequest.of(page, size, Sort.by("id").descending()));
        
        model.addAttribute("reservations", reservationsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reservationsPage.getTotalPages());
        model.addAttribute("totalItems", reservationsPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", ReservationStatus.values());
        
        return "admin/reservations";
    }

    @GetMapping("/reservations/approve/{id}")
    public String approveReservation(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.approveReservation(id);
            redirectAttributes.addFlashAttribute("successToast", "Book reservation request approved. Copy placed on hold.");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/admin/reservations";
    }

    @GetMapping("/reservations/cancel/{id}")
    public String cancelReservation(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.cancelReservation(id);
            redirectAttributes.addFlashAttribute("successToast", "Book reservation request cancelled.");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/admin/reservations";
    }

    @GetMapping("/reservations/fulfill/{id}")
    public String fulfillReservation(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.fulfillReservation(id);
            redirectAttributes.addFlashAttribute("successToast", "Reservation fulfilled. Borrow record created.");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/admin/reservations";
    }

    // ==========================================
    // REPORTS PAGE
    // ==========================================
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("totalBooks", bookService.countTotalBooks());
        model.addAttribute("totalMembers", memberService.countTotalMembers());
        model.addAttribute("activeBorrows", issueService.countActiveBorrows());
        model.addAttribute("overdueBooks", issueService.countOverdueBooks());
        model.addAttribute("activeReservations", reservationService.countActiveReservations());
        model.addAttribute("totalFineCollected", issueService.getTotalFineCollected());
        
        return "admin/reports";
    }
}
