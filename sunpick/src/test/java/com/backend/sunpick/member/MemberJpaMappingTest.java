package com.backend.sunpick.member;

import com.backend.sunpick.config.JpaConfig;
import com.backend.sunpick.domain.member.entity.Member;
import com.backend.sunpick.domain.member.repository.MemberRepository;
import com.backend.sunpick.factory.TestEntityFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
public class MemberJpaMappingTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 멤버_엔티티_필드_매핑_정상작동_테스트() {

        //given
        Member member = TestEntityFactory.testMember();
        Member savedMember = memberRepository.save(member);
        em.flush();
        em.clear();

        //when
        Member selectedMember = memberRepository.findById(savedMember.getId()).orElseThrow();

        //then
        assertSoftly(as -> {
            as.assertThat(selectedMember.getName()).isEqualTo("이영석");
            as.assertThat(selectedMember.getEmail()).isEqualTo("ssafy@naver.com");
            as.assertThat(selectedMember.getPassword()).isEqualTo("password123");
            as.assertThat(selectedMember.getBirthDate()).isEqualTo(LocalDate.of(1999, 1, 15));
            as.assertThat(selectedMember.getWithdrawnAt()).isNull();
            as.assertThat(selectedMember.getCreatedAt()).isNotNull();
            as.assertThat(selectedMember.getModifiedAt()).isNotNull();
        });
    }
}
