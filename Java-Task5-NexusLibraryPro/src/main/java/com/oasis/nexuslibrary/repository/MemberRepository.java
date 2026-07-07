package com.oasis.nexuslibrary.repository;

import com.oasis.nexuslibrary.entity.Member;
import com.oasis.nexuslibrary.entity.MemberStatus;
import com.oasis.nexuslibrary.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    Optional<Member> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    long countByRole(Role role);

    @Query("SELECT m FROM Member m WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(m.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " m.phone LIKE CONCAT('%', :search, '%')) AND " +
           "(:role IS NULL OR m.role = :role) AND " +
           "(:status IS NULL OR m.status = :status)")
    Page<Member> findMembersFiltered(@Param("search") String search, 
                                     @Param("role") Role role, 
                                     @Param("status") MemberStatus status, 
                                     Pageable pageable);
}
