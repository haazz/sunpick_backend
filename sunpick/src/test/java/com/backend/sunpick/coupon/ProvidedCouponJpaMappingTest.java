package com.backend.sunpick.coupon;

import com.backend.sunpick.config.JpaConfig;
import com.backend.sunpick.domain.coupon.entity.Coupon;
import com.backend.sunpick.domain.coupon.entity.CouponIssuer;
import com.backend.sunpick.domain.coupon.entity.ProvidedCoupon;
import com.backend.sunpick.domain.coupon.repository.CouponIssuerRepository;
import com.backend.sunpick.domain.coupon.repository.CouponRepository;
import com.backend.sunpick.domain.coupon.repository.ProvidedCouponRepository;
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
public class ProvidedCouponJpaMappingTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProvidedCouponRepository providedCouponRepository;

    @Autowired
    private CouponIssuerRepository issuerRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MemberRepository memberRepository;


    @Test
    void 제공된쿠폰_엔티티_필드_및_연관관계_정상작동_테스트() {

        // given
        Member member = TestEntityFactory.testMember();
        Member savedMember = memberRepository.save(member);
        CouponIssuer couponIssuer = TestEntityFactory.testCouponIssuer();
        CouponIssuer savedCouponIssuer = issuerRepository.save(couponIssuer);
        Coupon coupon = TestEntityFactory.testCoupon(savedCouponIssuer);
        Coupon savedCoupon = couponRepository.save(coupon);

        ProvidedCoupon providedCoupon = TestEntityFactory
            .testProvidedCoupon(savedMember, savedCoupon);
        ProvidedCoupon savedProvidedCoupon = providedCouponRepository.save(providedCoupon);
        em.flush();
        em.clear();

        // when
        ProvidedCoupon selectedProvidedCoupon = providedCouponRepository.findById(
            savedProvidedCoupon.getId()).orElseThrow();

        // then
        assertSoftly(as -> {
            as.assertThat(selectedProvidedCoupon.getMember().getId())
                .isEqualTo(savedMember.getId());
            as.assertThat(selectedProvidedCoupon.getCoupon().getId())
                .isEqualTo(savedCoupon.getId());
            as.assertThat(selectedProvidedCoupon.getCoupon().getCouponIssuer().getId())
                .isEqualTo(savedCouponIssuer.getId());
            as.assertThat(selectedProvidedCoupon.getUsedDate()).isNull();
            as.assertThat(selectedProvidedCoupon.getExpiredDate())
                .isEqualTo(LocalDate.of(2025, 8, 15));
            as.assertThat(selectedProvidedCoupon.getCreatedAt()).isNotNull();
            as.assertThat(selectedProvidedCoupon.getModifiedAt()).isNotNull();
        });
    }
}
