package com.backend.sunpick.coupon;

import com.backend.sunpick.config.JpaConfig;
import com.backend.sunpick.domain.coupon.entity.CouponIssuer;
import com.backend.sunpick.domain.coupon.repository.CouponIssuerRepository;
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
public class CouponIssuerJpaMappingTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CouponIssuerRepository issuerRepository;

    @Test
    void 쿠폰발행자_엔티티_필드_매핑_정상작동_테스트() {

        // given
        CouponIssuer couponIssuer = TestEntityFactory.testCouponIssuer();
        CouponIssuer savedCouponIssuer = issuerRepository.save(couponIssuer);
        em.flush();
        em.clear();

        // when
        CouponIssuer selectedCouponIssuer = issuerRepository.findById(savedCouponIssuer.getId())
            .orElseThrow();

        // then
        assertSoftly(as -> {
            as.assertThat(selectedCouponIssuer.getName()).isEqualTo("SunPick");
            as.assertThat(selectedCouponIssuer.getQuantity()).isEqualTo(1000);
            as.assertThat(selectedCouponIssuer.getCreatedAt()).isNotNull();
            as.assertThat(selectedCouponIssuer.getModifiedAt()).isNotNull();
        });
    }
}
