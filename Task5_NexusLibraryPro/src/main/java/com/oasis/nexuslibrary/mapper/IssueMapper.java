package com.oasis.nexuslibrary.mapper;

import com.oasis.nexuslibrary.dto.IssueDto;
import com.oasis.nexuslibrary.entity.Issue;

public class IssueMapper {

    public static IssueDto toDto(Issue issue) {
        if (issue == null) return null;
        return new IssueDto(
                issue.getId(),
                issue.getBook() != null ? issue.getBook().getId() : null,
                issue.getBook() != null ? issue.getBook().getTitle() : null,
                issue.getBook() != null ? issue.getBook().getIsbn() : null,
                issue.getMember() != null ? issue.getMember().getId() : null,
                issue.getMember() != null ? issue.getMember().getName() : null,
                issue.getMember() != null ? issue.getMember().getEmail() : null,
                issue.getIssueDate(),
                issue.getDueDate(),
                issue.getReturnDate(),
                issue.getFine(),
                issue.getLateDays(),
                issue.getStatus()
        );
    }
}
