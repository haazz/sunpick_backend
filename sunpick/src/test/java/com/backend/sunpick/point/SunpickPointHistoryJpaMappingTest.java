package com.backend.sunpick.point;

import com.backend.sunpick.config.JpaConfig;
import com.backend.sunpick.domain.member.entity.Member;
import com.backend.sunpick.domain.member.repository.MemberRepository;
import com.backend.sunpick.domain.point.entity.SunpickPointHistory;
import com.backend.sunpick.domain.point.repository.SunpickPointHistoryRepository;
import com.backend.sunpick.factory.TestEntityFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
public class SunpickPointHistoryJpaMappingTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private SunpickPointHistoryRepository pointHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 선픽포인트내역_엔티티_필드_및_연관관계_매핑_정상작동_테스트() {

        // given
        Member member = TestEntityFactory.testMember();
        Member savedMember = memberRepository.save(member);

        SunpickPointHistory sunpickPointHistory = TestEntityFactory
            .testSunpickPointHistory(savedMember);
        SunpickPointHistory savedPointHistory = pointHistoryRepository.save(sunpickPointHistory);
        em.flush();
        em.clear();

        // when
        SunpickPointHistory selectedPointHistory = pointHistoryRepository
            .findById(savedPointHistory.getId()).orElseThrow();

        // then
        assertSoftly(as -> {
            as.assertThat(selectedPointHistory.getMember().getId())
                .isEqualTo(savedPointHistory.getMember().getId());
            as.assertThat(selectedPointHistory.getChangePoint())
                .isEqualTo(-10000);
            as.assertThat(selectedPointHistory.getCreatedAt()).isNotNull();
            as.assertThat(selectedPointHistory.getModifiedAt()).isNotNull();
        });
    }
}
