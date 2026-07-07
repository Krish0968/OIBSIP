package com.oasis.nexuslibrary.mapper;

import com.oasis.nexuslibrary.dto.MemberDto;
import com.oasis.nexuslibrary.entity.Member;

public class MemberMapper {

    public static MemberDto toDto(Member member) {
        if (member == null) return null;
        return new MemberDto(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getPhone(),
                null, // Do not return the hashed password in DTO
                member.getRole(),
                member.getMembershipDate(),
                member.getAddress(),
                member.getStatus()
        );
    }

    public static Member toEntity(MemberDto dto) {
        if (dto == null) return null;
        return new Member(
                dto.getId(),
                dto.getName(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getPassword(),
                dto.getRole(),
                dto.getMembershipDate(),
                dto.getAddress(),
                dto.getStatus()
        );
    }
}
