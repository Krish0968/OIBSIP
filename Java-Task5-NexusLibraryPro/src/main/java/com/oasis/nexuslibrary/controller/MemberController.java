package com.oasis.nexuslibrary.controller;

import com.oasis.nexuslibrary.dto.BookDto;
import com.oasis.nexuslibrary.dto.IssueDto;
import com.oasis.nexuslibrary.dto.MemberDto;
import com.oasis.nexuslibrary.dto.ReservationDto;
import com.oasis.nexuslibrary.exception.BadRequestException;
import com.oasis.nexuslibrary.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/member")
public class MemberController {

    private final BookService bookService;
    private final MemberService memberService;
    private final IssueService issueService;
    private final ReservationService reservationService;
    private final ReportService reportService;
    private final CategoryService categoryService;

    public MemberController(BookService bookService, MemberService memberService, IssueService issueService,
                            ReservationService reservationService, ReportService reportService, CategoryService categoryService) {
        this.bookService = bookService;
        this.memberService = memberService;
        this.issueService = issueService;
        this.reservationService = reservationService;
        this.reportService = reportService;
        this.categoryService = categoryService;
    }

    private MemberDto getLoggedInMember(Principal principal) {
        return memberService.findByEmail(principal.getName());
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        MemberDto member = getLoggedInMember(principal);
        Map<String, Object> stats = reportService.getMemberDashboardStats(member.getId());
        
        model.addAttribute("member", member);
        model.addAllAttributes(stats);
        
        return "member/dashboard";
    }

    // ==========================================
    // CATALOG & DETAILS
    // ==========================================
    @GetMapping("/catalog")
    public String catalog(@RequestParam(value = "search", required = false, defaultValue = "") String search,
                          @RequestParam(value = "categoryId", required = false) Long categoryId,
                          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                          @RequestParam(value = "size", required = false, defaultValue = "12") int size,
                          Model model) {
        Page<BookDto> booksPage = bookService.findFiltered(search, categoryId, null, null, PageRequest.of(page, size, Sort.by("id").descending()));
        
        model.addAttribute("books", booksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", booksPage.getTotalPages());
        model.addAttribute("totalItems", booksPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("selectedCategoryId", categoryId);
        
        model.addAttribute("categories", categoryService.findAll());
        
        return "member/catalog";
    }

    @GetMapping("/books/{id}")
    public String bookDetails(@PathVariable("id") Long id, Principal principal, Model model) {
        BookDto book = bookService.findById(id);
        MemberDto member = getLoggedInMember(principal);
        
        // Check member's active relations with this book to show correct UI buttons
        List<IssueDto> activeBorrows = issueService.findMemberActiveBorrows(member.getId());
        List<ReservationDto> activeReservations = reservationService.findMemberActiveReservations(member.getId());

        boolean isAlreadyBorrowed = activeBorrows.stream()
                .anyMatch(i -> i.getBookId().equals(id) && i.getStatus().name().equals("ISSUED"));
        
        boolean isAlreadyRequested = activeBorrows.stream()
                .anyMatch(i -> i.getBookId().equals(id) && i.getStatus().name().equals("REQUESTED"));

        boolean isAlreadyReturnRequested = activeBorrows.stream()
                .anyMatch(i -> i.getBookId().equals(id) && i.getStatus().name().equals("RETURN_REQUESTED"));
                
        boolean isAlreadyReserved = activeReservations.stream()
                .anyMatch(r -> r.getBookId().equals(id));

        model.addAttribute("book", book);
        model.addAttribute("isBorrowed", isAlreadyBorrowed);
        model.addAttribute("isRequested", isAlreadyRequested);
        model.addAttribute("isReturnRequested", isAlreadyReturnRequested);
        model.addAttribute("isReserved", isAlreadyReserved);

        // Find active checkout ID if already borrowed (needed for requesting returns)
        if (isAlreadyBorrowed || isAlreadyReturnRequested) {
            IssueDto activeIssue = activeBorrows.stream()
                    .filter(i -> i.getBookId().equals(id))
                    .findFirst().orElse(null);
            model.addAttribute("activeIssueId", activeIssue != null ? activeIssue.getId() : null);
        }
        
        // Find reservation ID if reserved (needed for cancellation)
        if (isAlreadyReserved) {
            ReservationDto activeRes = activeReservations.stream()
                    .filter(r -> r.getBookId().equals(id))
                    .findFirst().orElse(null);
            model.addAttribute("activeReservationId", activeRes != null ? activeRes.getId() : null);
        }

        return "member/book-details";
    }

    // ==========================================
    // BORROW REQUESTS
    // ==========================================
    @PostMapping("/borrow/request/{bookId}")
    public String requestBorrow(@PathVariable("bookId") Long bookId, Principal principal, RedirectAttributes redirectAttributes) {
        MemberDto member = getLoggedInMember(principal);
        try {
            issueService.requestBorrow(member.getId(), bookId);
            redirectAttributes.addFlashAttribute("successToast", "Borrow request submitted successfully! Pending administrator approval.");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/member/books/" + bookId;
    }

    @PostMapping("/borrow/return/{issueId}")
    public String requestReturn(@PathVariable("issueId") Long issueId, RedirectAttributes redirectAttributes) {
        try {
            IssueDto issue = issueService.requestReturn(issueId);
            redirectAttributes.addFlashAttribute("successToast", "Return request submitted! Please return the book physical copy to the library desk.");
            return "redirect:/member/books/" + issue.getBookId();
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
            return "redirect:/member/dashboard";
        }
    }

    // ==========================================
    // RESERVATIONS
    // ==========================================
    @PostMapping("/reserve/request/{bookId}")
    public String requestReservation(@PathVariable("bookId") Long bookId, Principal principal, RedirectAttributes redirectAttributes) {
        MemberDto member = getLoggedInMember(principal);
        try {
            reservationService.createReservation(member.getId(), bookId);
            redirectAttributes.addFlashAttribute("successToast", "Reservation request placed! We will hold a copy for you when available.");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/member/books/" + bookId;
    }

    @PostMapping("/reserve/cancel/{reservationId}")
    public String cancelReservation(@PathVariable("reservationId") Long reservationId, RedirectAttributes redirectAttributes) {
        try {
            reservationService.cancelReservation(reservationId);
            redirectAttributes.addFlashAttribute("successToast", "Reservation request cancelled.");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/member/dashboard";
    }

    // ==========================================
    // PROFILE MANAGEMENT
    // ==========================================
    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        MemberDto member = getLoggedInMember(principal);
        model.addAttribute("member", member);
        return "member/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("member") MemberDto profileDto, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            memberService.updateProfile(principal.getName(), profileDto);
            redirectAttributes.addFlashAttribute("successToast", "Profile information updated.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorToast", "Failed to update profile: " + ex.getMessage());
        }
        return "redirect:/member/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        try {
            memberService.changePassword(principal.getName(), currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("successToast", "Password changed successfully.");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorToast", ex.getMessage());
        }
        return "redirect:/member/profile";
    }

    @GetMapping("/history")
    public String borrowHistory(Principal principal,
                                @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                Model model) {
        MemberDto member = getLoggedInMember(principal);
        Page<IssueDto> borrowHistory = issueService.findMemberHistory(member.getId(), PageRequest.of(page, size, Sort.by("id").descending()));
        Page<ReservationDto> reservationHistory = reservationService.findMemberReservations(member.getId(), PageRequest.of(page, size, Sort.by("id").descending()));

        model.addAttribute("borrows", borrowHistory.getContent());
        model.addAttribute("borrowCurrentPage", page);
        model.addAttribute("borrowTotalPages", borrowHistory.getTotalPages());
        
        model.addAttribute("reservations", reservationHistory.getContent());
        
        return "member/history";
    }
}
