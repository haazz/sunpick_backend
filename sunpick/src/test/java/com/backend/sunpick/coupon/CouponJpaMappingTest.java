package com.backend.sunpick.coupon;

import com.backend.sunpick.config.JpaConfig;
import com.backend.sunpick.domain.coupon.entity.Coupon;
import com.backend.sunpick.domain.coupon.entity.CouponIssuer;
import com.backend.sunpick.domain.coupon.enums.DiscountType;
import com.backend.sunpick.domain.coupon.repository.CouponIssuerRepository;
import com.backend.sunpick.domain.coupon.repository.CouponRepository;
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
public class CouponJpaMappingTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponIssuerRepository issuerRepository;

    @Test
    void 쿠폰_엔티티_필드_및_연관관계_매핑_정상작동_테스트() {

        // given
        CouponIssuer couponIssuer = TestEntityFactory.testCouponIssuer();
        CouponIssuer savedCouponIssuer = issuerRepository.save(couponIssuer);
        Coupon coupon = TestEntityFactory.testCoupon(savedCouponIssuer);
        Coupon savedCoupon = couponRepository.save(coupon);
        em.flush();
        em.clear();

        // when
        Coupon selectedCoupon = couponRepository.findById(savedCoupon.getId()).orElseThrow();

        // then
        assertSoftly(as -> {
            as.assertThat(selectedCoupon.getCouponIssuer().getId()).isEqualTo(savedCouponIssuer.getId());
            as.assertThat(selectedCoupon.getName()).isEqualTo("선픽 개봉기념 할인쿠폰");
            as.assertThat(selectedCoupon.getValidDays()).isEqualTo(31);
            as.assertThat(selectedCoupon.getDiscountType()).isEqualTo(DiscountType.PIXED);
            as.assertThat(selectedCoupon.getDiscountWeight()).isEqualTo(1000);
            as.assertThat(selectedCoupon.getCreatedAt()).isNotNull();
            as.assertThat(selectedCoupon.getModifiedAt()).isNotNull();
        });
    }
}
