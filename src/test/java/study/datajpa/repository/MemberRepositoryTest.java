package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.awt.print.Pageable;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void testCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).orElse(null);
        Member findMember2 = memberRepository.findById(member2.getId()).orElse(null);

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findUsername() {
        Member member = new Member("member");
        memberRepository.save(member);

        Member findMember = memberRepository.findByUsername("member");

        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void namedQuery() {
        Member member = new Member("member");
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> findMember = memberRepository.findUsernameInNamedQuery("member");
        assertThat(findMember.get(0)).isEqualTo(member);

        List<Member> findMember1 = memberRepository.findUsernameInNoNamedQuery("member1");
        assertThat(findMember1.get(0)).isEqualTo(member1);

        List<String> userNames = memberRepository.findUserNames();
        assertThat(userNames.size()).isEqualTo(3);
        assertThat(userNames).containsExactly("member", "member1", "member2");
    }

    @Test
    public void dtoQuery() {
        Team team1 = new Team("team1");
        Team team2 = new Team("team2");
        teamRepository.save(team1);
        teamRepository.save(team2);

        Member member = new Member("member");
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        member.changeTeam(team1);
        member1.changeTeam(team1);
        member2.changeTeam(team2);
        memberRepository.save(member);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        assertThat(memberDto.size()).isEqualTo(3);
        assertThat(memberDto).extracting("teamName").contains("team1", "team2");
        assertThat(memberDto).extracting("teamName", "username")
            .contains(tuple("team1", "member"), tuple("team1", "member1"), tuple("team2", "member2"));
    }

    @Test
    public void findByNames() {
        Member member = new Member("member");
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByNames(Arrays.asList("member", "member1"));
        assertThat(members.size()).isEqualTo(2);
        assertThat(members).extracting("username").contains("member", "member1");
    }

    @Test
    public void page() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));


        int age = 10;
        int offset = 0;
        int limit = 3;

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> members = memberRepository.findByAge(age, pageRequest);

        assertThat(members.getTotalElements()).isEqualTo(5);
        assertThat(members.hasNext()).isTrue();
        assertThat(members.isFirst()).isTrue();
        assertThat(members.getTotalPages()).isEqualTo(2);
        assertThat(members.getNumberOfElements()).isEqualTo(3);

    }
}