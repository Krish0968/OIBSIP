package com.oasis.nexuslibrary.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.oasis.nexuslibrary.dto.BookDto;
import com.oasis.nexuslibrary.dto.IssueDto;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfReportGenerator {

    public static void generateLibraryReport(OutputStream out, List<BookDto> books, List<IssueDto> activeIssues) throws IOException {
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts configuration
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.WHITE);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(0, 150, 255));
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

            // Title Banner Card
            PdfPTable bannerTable = new PdfPTable(1);
            bannerTable.setWidthPercentage(100);
            PdfPCell bannerCell = new PdfPCell(new Paragraph("NEXUSLIBRARY PRO - SYSTEM REPORT", titleFont));
            bannerCell.setBackgroundColor(new Color(30, 30, 30));
            bannerCell.setPadding(15);
            bannerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            bannerCell.setBorder(Rectangle.NO_BORDER);
            bannerTable.addCell(bannerCell);
            document.add(bannerTable);

            // Metadata info
            Paragraph meta = new Paragraph();
            meta.add(new Chunk("\nReport Generated On: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"))));
            meta.add(new Chunk("\nTotal Cataloged Books: " + books.size()));
            meta.add(new Chunk("\nTotal Active Checkouts: " + activeIssues.size() + "\n\n"));
            meta.setFont(textFont);
            document.add(meta);

            // Section 1: Books Catalog
            document.add(new Paragraph("1. Books Catalog Status", sectionFont));
            document.add(new Paragraph(" "));

            PdfPTable bookTable = new PdfPTable(new float[]{1.5f, 3f, 2f, 2f, 1f});
            bookTable.setWidthPercentage(100);
            
            String[] bookHeaders = {"ISBN", "Title", "Author", "Category", "Copies"};
            for (String header : bookHeaders) {
                PdfPCell cell = new PdfPCell(new Paragraph(header, headerFont));
                cell.setBackgroundColor(new Color(0, 100, 200));
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                bookTable.addCell(cell);
            }

            for (BookDto book : books) {
                bookTable.addCell(new PdfPCell(new Paragraph(book.getIsbn(), textFont)));
                bookTable.addCell(new PdfPCell(new Paragraph(book.getTitle(), textFont)));
                bookTable.addCell(new PdfPCell(new Paragraph(book.getAuthorName(), textFont)));
                bookTable.addCell(new PdfPCell(new Paragraph(book.getCategoryName(), textFont)));
                
                PdfPCell copiesCell = new PdfPCell(new Paragraph(book.getAvailableCopies() + "/" + book.getTotalCopies(), textFont));
                copiesCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                bookTable.addCell(copiesCell);
            }
            document.add(bookTable);

            // Section 2: Active Issues
            document.newPage();
            document.add(new Paragraph("2. Active Checkouts & Overdues", sectionFont));
            document.add(new Paragraph(" "));

            if (activeIssues.isEmpty()) {
                document.add(new Paragraph("No active checkouts found.", textFont));
            } else {
                PdfPTable issueTable = new PdfPTable(new float[]{2f, 2.5f, 1.5f, 1.5f, 1f, 1f});
                issueTable.setWidthPercentage(100);
                
                String[] issueHeaders = {"Member", "Book Title", "Checkout Date", "Due Date", "Late Days", "Fine"};
                for (String header : issueHeaders) {
                    PdfPCell cell = new PdfPCell(new Paragraph(header, headerFont));
                    cell.setBackgroundColor(new Color(220, 53, 69));
                    cell.setPadding(5);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    issueTable.addCell(cell);
                }

                for (IssueDto issue : activeIssues) {
                    issueTable.addCell(new PdfPCell(new Paragraph(issue.getMemberName(), textFont)));
                    issueTable.addCell(new PdfPCell(new Paragraph(issue.getBookTitle(), textFont)));
                    issueTable.addCell(new PdfPCell(new Paragraph(issue.getIssueDate().toString(), textFont)));
                    issueTable.addCell(new PdfPCell(new Paragraph(issue.getDueDate().toString(), textFont)));
                    
                    PdfPCell lateCell = new PdfPCell(new Paragraph(String.valueOf(issue.getLateDays()), textFont));
                    lateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    issueTable.addCell(lateCell);

                    PdfPCell fineCell = new PdfPCell(new Paragraph("₹" + String.format("%.2f", issue.getFine()), textFont));
                    fineCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    issueTable.addCell(fineCell);
                }
                document.add(issueTable);
            }

            document.close();
        } catch (DocumentException e) {
            throw new IOException(e.getMessage());
        }
    }
}
