package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    //spring이 interface를 통해 proxy 객체를 만들어서 di
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    @Test
    public void testMember() {
        Member member = new Member("hello");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo((member.getId()));
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건조회검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //list 조회
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // count검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //delte 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(members.size()).isEqualTo(1);
        assertThat(members.get(0).getUsername()).isEqualTo("AAA");
        assertThat(members.get(0).getAge()).isEqualTo(20);
    }

    @Test
    void queryAnnotationTest() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> members = memberRepository.findUser("AAA", 20);

        assertThat(members.size()).isEqualTo(1);
        assertThat(members.get(0).getUsername()).isEqualTo("AAA");
        assertThat(members.get(0).getAge()).isEqualTo(20);

    }

    @Test
    void findUsernameListTest() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernames = memberRepository.findUsernameList();
        for (String username : usernames) {
            System.out.println("username = " + username);
        }
    }

    @Test
    void findMemeberDtoTest() {
        Member m1 = new Member("AAA", 10);
        Team t1 = new Team("teamA");
        teamRepository.save(t1);
        m1.setTeam(t1);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    void findByNamesTest() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    void returnTypeTest() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        Member aaa = memberRepository.findMemberByUsername("AAA");
        List<Member> aaa1 = memberRepository.findListByUsername("AAA");
        //list 반환시 데이터가 없으면 empty 클래스가 반환됨
        Optional<Member> aaa2 = memberRepository.findOptionalByUsername("AAA");

        assertThat(aaa).isEqualTo(aaa1.get(0));
        assertThat(aaa).isEqualTo(aaa2.get());
    }
}