package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByUsername(String username);

    @Query(name = "Member.findUsernameInNamedQuery")
    List<Member> findUsernameInNamedQuery(@Param("username") String username);

    @Query("select m from Member m where m.username = :username")
    List<Member> findUsernameInNoNamedQuery(@Param("username") String username);

    @Query("select m.username from Member m")
    List<String> findUserNames();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    Page<Member> findByAge(int age, Pageable pageable);
}
