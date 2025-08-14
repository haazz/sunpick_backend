package com.backend.sunpick.exclusive;

import com.backend.sunpick.config.JpaConfig;
import com.backend.sunpick.domain.exclusive.entity.ExclusiveProduct;
import com.backend.sunpick.domain.exclusive.repository.ExclusiveProductRepository;
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
public class ExclusiveProductJpaMappingTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ExclusiveProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    void 한정판상품_엔티티_필드_및_연관관계_정상작동_테스트() {

        // given
        Member member = TestEntityFactory.testMember();
        Member savedMember = memberRepository.save(member);

        Store store = TestEntityFactory.testStore(savedMember);
        Store savedStore = storeRepository.save(store);

        ExclusiveProduct exclusiveProduct = TestEntityFactory.testExclusiveProduct(savedStore);
        ExclusiveProduct savedExclusiveProduct = productRepository.save(exclusiveProduct);
        em.flush();
        em.clear();

        // when
        ExclusiveProduct selectedExclusiveProduct = productRepository.findById(
            savedExclusiveProduct.getId()).orElseThrow();

        // then
        assertSoftly(as -> {
            as.assertThat(selectedExclusiveProduct.getStore().getId())
                .isEqualTo(savedStore.getId());
            as.assertThat(selectedExclusiveProduct.getStore().getMember().getId())
                .isEqualTo(savedMember.getId());
            as.assertThat(selectedExclusiveProduct.getName()).isEqualTo("주먹밥");
            as.assertThat(selectedExclusiveProduct.getPrice()).isEqualTo(2000);
            as.assertThat(selectedExclusiveProduct.getQuantity()).isEqualTo(1000);
            as.assertThat(selectedExclusiveProduct.getCreatedAt()).isNotNull();
            as.assertThat(selectedExclusiveProduct.getModifiedAt()).isNotNull();
        });
    }
}
