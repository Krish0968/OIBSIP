package com.oasis.nexuslibrary.util;

import com.oasis.nexuslibrary.dto.BookDto;
import com.oasis.nexuslibrary.dto.IssueDto;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CsvReportGenerator {

    public static void generateBooksCsv(Writer writer, List<BookDto> books) throws IOException {
        writer.write("Book ID,ISBN,Title,Author,Category,Publisher,Language,Publication Year,Available Copies,Total Copies,Shelf Number,Status\n");
        for (BookDto book : books) {
            writer.write(String.format("%d,%s,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d,%d,%d,\"%s\",%s\n",
                    book.getId(),
                    escapeCsv(book.getIsbn()),
                    escapeCsv(book.getTitle()),
                    escapeCsv(book.getAuthorName()),
                    escapeCsv(book.getCategoryName()),
                    escapeCsv(book.getPublisher()),
                    escapeCsv(book.getLanguage()),
                    book.getPublicationYear(),
                    book.getAvailableCopies(),
                    book.getTotalCopies(),
                    escapeCsv(book.getShelfNumber()),
                    book.getStatus().name()
            ));
        }
    }

    public static void generateIssuesCsv(Writer writer, List<IssueDto> activeIssues) throws IOException {
        writer.write("Checkout ID,Member Name,Member Email,Book Title,ISBN,Checkout Date,Due Date,Return Date,Late Days,Fine Amount,Status\n");
        for (IssueDto issue : activeIssues) {
            writer.write(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",%s,%s,%s,%d,%.2f,%s\n",
                    issue.getId(),
                    escapeCsv(issue.getMemberName()),
                    escapeCsv(issue.getMemberEmail()),
                    escapeCsv(issue.getBookTitle()),
                    escapeCsv(issue.getBookIsbn()),
                    issue.getIssueDate(),
                    issue.getDueDate(),
                    issue.getReturnDate() != null ? issue.getReturnDate().toString() : "N/A",
                    issue.getLateDays(),
                    issue.getFine(),
                    issue.getStatus().name()
            ));
        }
    }

    private static String escapeCsv(String val) {
        if (val == null) return "";
        return val.replace("\"", "\"\"");
    }
}
