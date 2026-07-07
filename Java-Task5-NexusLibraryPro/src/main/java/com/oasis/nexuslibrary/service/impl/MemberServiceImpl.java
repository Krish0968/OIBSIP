package com.oasis.nexuslibrary.service.impl;

import com.oasis.nexuslibrary.dto.MemberDto;
import com.oasis.nexuslibrary.entity.Member;
import com.oasis.nexuslibrary.entity.MemberStatus;
import com.oasis.nexuslibrary.entity.Role;
import com.oasis.nexuslibrary.exception.BadRequestException;
import com.oasis.nexuslibrary.exception.ResourceNotFoundException;
import com.oasis.nexuslibrary.mapper.MemberMapper;
import com.oasis.nexuslibrary.repository.MemberRepository;
import com.oasis.nexuslibrary.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public MemberDto save(MemberDto memberDto) {
        if (memberRepository.existsByEmail(memberDto.getEmail())) {
            throw new BadRequestException("Email address '" + memberDto.getEmail() + "' is already registered.");
        }

        Member member = MemberMapper.toEntity(memberDto);
        member.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        
        // Default values for registration
        if (member.getRole() == null) {
            member.setRole(Role.MEMBER);
        }
        member.setMembershipDate(LocalDate.now());
        member.setStatus(MemberStatus.ACTIVE);

        Member savedMember = memberRepository.save(member);
        return MemberMapper.toDto(savedMember);
    }

    @Override
    public MemberDto update(Long id, MemberDto memberDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));

        memberRepository.findByEmail(memberDto.getEmail())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BadRequestException("Email address '" + memberDto.getEmail() + "' is already in use.");
                    }
                });

        member.setName(memberDto.getName());
        member.setEmail(memberDto.getEmail());
        member.setPhone(memberDto.getPhone());
        member.setAddress(memberDto.getAddress());
        
        if (memberDto.getRole() != null) {
            member.setRole(memberDto.getRole());
        }
        if (memberDto.getStatus() != null) {
            member.setStatus(memberDto.getStatus());
        }
        
        // Optionally update password if provided
        if (memberDto.getPassword() != null && !memberDto.getPassword().isBlank()) {
            member.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        }

        Member updatedMember = memberRepository.save(member);
        return MemberMapper.toDto(updatedMember);
    }

    @Override
    public void delete(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));

        // Enforce checkout restrictions
        boolean hasActiveBorrows = member.getIssues().stream()
                .anyMatch(issue -> issue.getStatus().name().equals("ISSUED") || issue.getStatus().name().equals("OVERDUE"));
        if (hasActiveBorrows) {
            throw new BadRequestException("Cannot delete member because they currently have checked out books.");
        }

        memberRepository.delete(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDto findById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
        return MemberMapper.toDto(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDto findByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with email: " + email));
        return MemberMapper.toDto(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberDto> findFiltered(String search, Role role, MemberStatus status, Pageable pageable) {
        return memberRepository.findMembersFiltered(search, role, status, pageable)
                .map(MemberMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalMembers() {
        return memberRepository.countByRole(Role.MEMBER);
    }

    @Override
    public MemberDto updateProfile(String email, MemberDto profileDto) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with email: " + email));

        member.setName(profileDto.getName());
        member.setPhone(profileDto.getPhone());
        member.setAddress(profileDto.getAddress());

        Member updatedMember = memberRepository.save(member);
        return MemberMapper.toDto(updatedMember);
    }

    @Override
    public void changePassword(String email, String currentPassword, String newPassword) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with email: " + email));

        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new BadRequestException("Current password does not match.");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new BadRequestException("New password must be at least 6 characters.");
        }

        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }
}
