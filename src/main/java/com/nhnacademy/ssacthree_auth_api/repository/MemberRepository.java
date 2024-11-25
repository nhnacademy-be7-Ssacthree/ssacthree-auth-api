package com.nhnacademy.ssacthree_auth_api.repository;


import com.nhnacademy.ssacthree_auth_api.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByMemberLoginId(String memberLoginId);

    Member findByMemberLoginId(String memberLoginId);

    Optional<Member> findByPaycoIdNumber(String paycoIdNumber);
}
