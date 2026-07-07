package com.oasis.nexuslibrary.controller;

import com.oasis.nexuslibrary.dto.BookDto;
import com.oasis.nexuslibrary.dto.IssueDto;
import com.oasis.nexuslibrary.entity.BookStatus;
import com.oasis.nexuslibrary.entity.IssueStatus;
import com.oasis.nexuslibrary.service.BookService;
import com.oasis.nexuslibrary.service.IssueService;
import com.oasis.nexuslibrary.util.CsvReportGenerator;
import com.oasis.nexuslibrary.util.PdfReportGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/reports/export")
public class ReportExportController {

    private final BookService bookService;
    private final IssueService issueService;

    public ReportExportController(BookService bookService, IssueService issueService) {
        this.bookService = bookService;
        this.issueService = issueService;
    }

    @GetMapping("/pdf")
    public void exportPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=nexuslibrary_report_" + LocalDate.now() + ".pdf");

        // Fetch all cataloged books (without pagination limit, retrieve first 500)
        List<BookDto> books = bookService.findFiltered("", null, null, null, PageRequest.of(0, 500, Sort.by("id").descending())).getContent();
        
        // Fetch all active borrows (issued + overdue + return requested)
        List<IssueDto> activeIssues = issueService.findFiltered("", null, PageRequest.of(0, 500, Sort.by("id").descending())).getContent()
                .stream()
                .filter(i -> i.getStatus() != IssueStatus.RETURNED)
                .toList();

        OutputStream out = response.getOutputStream();
        PdfReportGenerator.generateLibraryReport(out, books, activeIssues);
        out.flush();
    }

    @GetMapping("/books/csv")
    public void exportBooksCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=nexuslibrary_books_" + LocalDate.now() + ".csv");

        List<BookDto> books = bookService.findFiltered("", null, null, null, PageRequest.of(0, 1000, Sort.by("id").descending())).getContent();

        Writer writer = response.getWriter();
        CsvReportGenerator.generateBooksCsv(writer, books);
        writer.flush();
    }

    @GetMapping("/checkouts/csv")
    public void exportCheckoutsCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=nexuslibrary_checkouts_" + LocalDate.now() + ".csv");

        List<IssueDto> activeIssues = issueService.findFiltered("", null, PageRequest.of(0, 1000, Sort.by("id").descending())).getContent();

        Writer writer = response.getWriter();
        CsvReportGenerator.generateIssuesCsv(writer, activeIssues);
        writer.flush();
    }
}
