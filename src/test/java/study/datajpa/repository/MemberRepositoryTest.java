package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @PersistenceContext
    EntityManager em;
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

    @Test
    void pagingTest() {
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        int age = 10;

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //dto 변환
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername()));

        //then
        List<Member> members = page.getContent();
//        long totalCount = page.getTotalElements();
        assertThat(members.size()).isEqualTo(3);
//        assertThat(totalCount).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    void bulkUpdateTest() {
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",19));
        memberRepository.save(new Member("member3",20));
        memberRepository.save(new Member("member4",21));
        memberRepository.save(new Member("member5",40));

        //when
        // 벌크 연산 후에는 영속성 컨텍스트를 날리자
        int resultCount = memberRepository.bulkAgePlus(20);
        //em.clear();
        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    void findMemberLazy() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1",10,teamA);
        Member member2 = new Member("member2",10,teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        //select Member N + 1 문제 발생
        // 페치조인으로 해결
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            //select Team
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    void queryHint() {
        Member member1 = new Member("member1",19);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        // 변경 감지를 위해 메모리 낭비가 발생함.
        // readonly hint를 적용하면 변경감지를 사용하지않음.
        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername());
        findMember.setUsername("member2");

        // dirty checking 으로 업데이트 쿼리가 나감
        em.flush();

    }

    @Test
    void lockTest() {
        Member member1 = new Member("member1",19);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        // 변경 감지를 위해 메모리 낭비가 발생함.
        // readonly hint를 적용하면 변경감지를 사용하지않음.
        List<Member> result = memberRepository.findLockByUsername(member1.getUsername());
        //findMember.setUsername("member2");

        // dirty checking 으로 업데이트 쿼리가 나감
        em.flush();

    }

    @Test
    void callCustom() {
        List<Member> memberCustom = memberRepository.findMemberCustom();
    }

    @Test
    void projections() {
        Team teamA = new Team("teamA");
        em.persist(teamA);
        Member member1 = new Member("member1",19,teamA);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        List<UserNameOnlyDto> result = memberRepository.findProjectionsByUsername("member1");

        for (UserNameOnlyDto usernameOnly : result) {
            System.out.println("usernameOnly = " + usernameOnly.getUsername());
        }
    }

    @Test
    void nativeQuery() {
        Team teamA = new Team("teamA");
        em.persist(teamA);
        Member member1 = new Member("member1",10,teamA);
        Member member2 = new Member("member2",10,teamA);
        memberRepository.save(member1);
        memberRepository.save(member2);
        em.flush();
        em.clear();

        Page<MemberProejction> result = memberRepository.findByNativeProjection(PageRequest.of(1, 10));

        System.out.println("result = " + result);

    }
}