package com.backend.sunpick.exclusive;

import com.backend.sunpick.config.JpaConfig;
import com.backend.sunpick.domain.exclusive.entity.ExclusiveProduct;
import com.backend.sunpick.domain.exclusive.entity.ExclusiveSaleEvent;
import com.backend.sunpick.domain.exclusive.repository.ExclusiveProductRepository;
import com.backend.sunpick.domain.exclusive.repository.ExclusiveSaleEventRepository;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
public class ExclusiveSaleEventJpaMappingTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ExclusiveSaleEventRepository eventRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ExclusiveProductRepository productRepository;

    @Test
    void 한정수량판매이벤트_엔티티_필드_및_연관관계_정상작동_테스트() {

        // given
        Member savedMember = memberRepository.save(TestEntityFactory.testMember());
        Store savedStore = storeRepository.save(TestEntityFactory.testStore(savedMember));
        ExclusiveProduct savedExclusiveProduct = productRepository.save(
            TestEntityFactory.testExclusiveProduct(savedStore));

        ExclusiveSaleEvent exclusiveSaleEvent = TestEntityFactory
            .testExclusiveSaleEvent(savedStore, savedExclusiveProduct);
        ExclusiveSaleEvent savedExclusiveSaleEvent = eventRepository.save(exclusiveSaleEvent);
        em.flush();
        em.clear();

        // when
        ExclusiveSaleEvent selectedExclusiveSaleEvent = eventRepository.findById(
            savedExclusiveSaleEvent.getId()).orElseThrow();

        // then
        assertSoftly(as -> {
            as.assertThat(selectedExclusiveSaleEvent.getStore().getId())
                .isEqualTo(savedStore.getId());
            as.assertThat(selectedExclusiveSaleEvent.getExclusiveProduct().getId())
                .isEqualTo(savedExclusiveProduct.getId());
            as.assertThat(selectedExclusiveSaleEvent.getName()).isEqualTo("오픈기념 주먹밥 한정판매");
            as.assertThat(selectedExclusiveSaleEvent.getDescription()).isEqualTo("주먹밥 맛있어요 드셔보세요~");
            as.assertThat(selectedExclusiveSaleEvent.getOpenAt())
                .isEqualTo(LocalDateTime.of(2025, 8, 15, 12, 30));
            as.assertThat(selectedExclusiveSaleEvent.getCloseAt())
                .isEqualTo(LocalDateTime.of(2025, 9, 15, 12, 30));
            as.assertThat(selectedExclusiveSaleEvent.getCreatedAt()).isNotNull();
            as.assertThat(selectedExclusiveSaleEvent.getModifiedAt()).isNotNull();
        });
    }
}
