package com.oasis.nexuslibrary.service;

import com.oasis.nexuslibrary.dto.MemberDto;
import com.oasis.nexuslibrary.entity.MemberStatus;
import com.oasis.nexuslibrary.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {
    MemberDto save(MemberDto memberDto);
    MemberDto update(Long id, MemberDto memberDto);
    void delete(Long id);
    MemberDto findById(Long id);
    MemberDto findByEmail(String email);
    Page<MemberDto> findFiltered(String search, Role role, MemberStatus status, Pageable pageable);
    long countTotalMembers();
    MemberDto updateProfile(String email, MemberDto profileDto);
    void changePassword(String email, String currentPassword, String newPassword);
}
