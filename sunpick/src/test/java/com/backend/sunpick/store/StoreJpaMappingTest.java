package com.backend.sunpick.store;

import com.backend.sunpick.config.JpaConfig;
import com.backend.sunpick.domain.member.entity.Member;
import com.backend.sunpick.domain.member.repository.MemberRepository;
import com.backend.sunpick.domain.store.entity.Store;
import com.backend.sunpick.domain.store.repository.StoreRepository;
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
public class StoreJpaMappingTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 상점_엔티티_필드_및_연관관계_매핑_정상작동_테스트() {

        // given
        Member member = TestEntityFactory.testMember();
        Member savedMember = memberRepository.save(member);

        Store store = TestEntityFactory.testStore(savedMember);
        Store savedStore = storeRepository.save(store);
        em.flush();
        em.clear();

        // when
        Store selectedStore = storeRepository.findById(savedStore.getId()).orElseThrow();

        // then
        assertSoftly(as -> {
            as.assertThat(selectedStore.getMember().getId()).isEqualTo(savedMember.getId());
            as.assertThat(selectedStore.getName()).isEqualTo("선픽마켓");
            as.assertThat(selectedStore.getDescription()).isEqualTo("선픽마켓에 오신것을 환영합니다");
            as.assertThat(selectedStore.getOwnerName()).isEqualTo("이영석");
            as.assertThat(selectedStore.getCreatedAt()).isNotNull();
            as.assertThat(selectedStore.getModifiedAt()).isNotNull();
        });
    }
}
