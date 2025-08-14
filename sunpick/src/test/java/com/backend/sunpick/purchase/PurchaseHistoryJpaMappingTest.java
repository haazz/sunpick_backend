package com.backend.sunpick.purchase;

import com.backend.sunpick.config.JpaConfig;
import com.backend.sunpick.domain.coupon.entity.Coupon;
import com.backend.sunpick.domain.coupon.entity.CouponIssuer;
import com.backend.sunpick.domain.coupon.repository.CouponIssuerRepository;
import com.backend.sunpick.domain.coupon.repository.CouponRepository;
import com.backend.sunpick.domain.exclusive.entity.ExclusiveProduct;
import com.backend.sunpick.domain.exclusive.entity.ExclusiveSaleEvent;
import com.backend.sunpick.domain.exclusive.repository.ExclusiveProductRepository;
import com.backend.sunpick.domain.exclusive.repository.ExclusiveSaleEventRepository;
import com.backend.sunpick.domain.member.entity.Member;
import com.backend.sunpick.domain.member.repository.MemberRepository;
import com.backend.sunpick.domain.point.entity.SunpickPointHistory;
import com.backend.sunpick.domain.point.repository.SunpickPointHistoryRepository;
import com.backend.sunpick.domain.purchase.entity.PurchaseHistory;
import com.backend.sunpick.domain.purchase.enums.PurchaseType;
import com.backend.sunpick.domain.purchase.repository.PurchaseHistoryRepository;
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
public class PurchaseHistoryJpaMappingTest {

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CouponIssuerRepository issuerRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ExclusiveProductRepository productRepository;

    @Autowired
    private ExclusiveSaleEventRepository saleEventRepository;

    @Autowired
    private SunpickPointHistoryRepository pointHistoryRepository;

    @Test
    void 결제내역_엔티티_필드_및_연관관계_정상작동_테스트() {

        // given
        Member savedMember = memberRepository.save(TestEntityFactory.testMember());
        CouponIssuer savedCouponIssuer = issuerRepository.save(
            TestEntityFactory.testCouponIssuer());

        Coupon savedCoupon = couponRepository.save(TestEntityFactory.testCoupon(savedCouponIssuer));
        Store savedStore = storeRepository.save(TestEntityFactory.testStore(savedMember));

        ExclusiveProduct savedExclusiveProduct = productRepository.save(
            TestEntityFactory.testExclusiveProduct(savedStore));
        ExclusiveSaleEvent savedExclusiveSaleEvent = saleEventRepository.save(
            TestEntityFactory.testExclusiveSaleEvent(savedStore, savedExclusiveProduct));

        SunpickPointHistory savedSunpickPointHistory = pointHistoryRepository.save(
            TestEntityFactory.testSunpickPointHistory(savedMember));

        PurchaseHistory purchaseHistory = TestEntityFactory.testPurchaseHistory(savedMember,
            savedCoupon, savedExclusiveSaleEvent, savedSunpickPointHistory);

        PurchaseHistory savedPurchaseHistory = purchaseHistoryRepository.save(purchaseHistory);
        em.flush();
        em.clear();

        // when
        PurchaseHistory selectedPurchaseHistory = purchaseHistoryRepository.findById(
            savedPurchaseHistory.getId()).orElseThrow();

        // then
        assertSoftly(as -> {
            as.assertThat(selectedPurchaseHistory.getMember().getId())
                .isEqualTo(savedMember.getId());
            as.assertThat(selectedPurchaseHistory.getCoupon().getId())
                .isEqualTo(savedCoupon.getId());
            as.assertThat(selectedPurchaseHistory.getExclusiveSaleEvent().getExclusiveProduct()
                .getId()).isEqualTo(savedExclusiveProduct.getId());
            as.assertThat(selectedPurchaseHistory.getSunpickPointHistory().getId())
                .isEqualTo(savedSunpickPointHistory.getId());
            as.assertThat(selectedPurchaseHistory.getPrice()).isEqualTo(20000);
            as.assertThat(selectedPurchaseHistory.getPurchaseType())
                .isEqualTo(PurchaseType.CARD);
        });
    }
}
