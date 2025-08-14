package com.backend.sunpick.factory;

import com.backend.sunpick.domain.coupon.entity.Coupon;
import com.backend.sunpick.domain.coupon.entity.CouponIssuer;
import com.backend.sunpick.domain.coupon.entity.ProvidedCoupon;
import com.backend.sunpick.domain.coupon.enums.DiscountType;
import com.backend.sunpick.domain.exclusive.entity.ExclusiveProduct;
import com.backend.sunpick.domain.exclusive.entity.ExclusiveSaleEvent;
import com.backend.sunpick.domain.member.entity.Member;
import com.backend.sunpick.domain.point.entity.SunpickPointHistory;
import com.backend.sunpick.domain.purchase.entity.PurchaseHistory;
import com.backend.sunpick.domain.purchase.enums.PurchaseType;
import com.backend.sunpick.domain.store.entity.Store;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestEntityFactory {

    public static Member testMember() {
        return Member.builder()
            .name("이영석")
            .email("ssafy@naver.com")
            .password("password123")
            .birthDate(LocalDate.of(1999, 1, 15))
            .withdrawnAt(null)
            .build();
    }

    public static Store testStore(Member member) {
        return Store.builder()
            .name("선픽마켓")
            .member(member)
            .description("선픽마켓에 오신것을 환영합니다")
            .ownerName("이영석")
            .build();
    }

    public static Coupon testCoupon(CouponIssuer couponIssuer) {
        return Coupon.builder()
            .name("선픽 개봉기념 할인쿠폰")
            .couponIssuer(couponIssuer)
            .discountType(DiscountType.PIXED)
            .discountWeight(1000)
            .validDays(31)
            .build();
    }

    public static CouponIssuer testCouponIssuer() {
        return CouponIssuer.builder()
            .name("SunPick")
            .quantity(1000)
            .build();
    }

    public static ProvidedCoupon testProvidedCoupon(Member member, Coupon coupon) {
        return ProvidedCoupon.builder()
            .member(member)
            .coupon(coupon)
            .usedDate(null)
            .expiredDate(LocalDate.of(2025, 8, 15))
            .build();
    }

    public static ExclusiveProduct testExclusiveProduct(Store store) {
        return ExclusiveProduct.builder()
            .store(store)
            .name("주먹밥")
            .price(2000)
            .quantity(1000)
            .build();
    }

    public static ExclusiveSaleEvent testExclusiveSaleEvent(Store store,
        ExclusiveProduct exclusiveProduct) {
        return ExclusiveSaleEvent.builder()
            .store(store)
            .exclusiveProduct(exclusiveProduct)
            .name("오픈기념 주먹밥 한정판매")
            .description("주먹밥 맛있어요 드셔보세요~")
            .openAt(LocalDateTime.of(2025, 8, 15, 12, 30))
            .closeAt(LocalDateTime.of(2025, 9, 15, 12, 30))
            .build();
    }

    public static SunpickPointHistory testSunpickPointHistory(Member member){
        return SunpickPointHistory.builder()
            .member(member)
            .changePoint(-10000)
            .build();
    }

    public static PurchaseHistory testPurchaseHistory(Member member, Coupon coupon,
        ExclusiveSaleEvent exclusiveSaleEvent, SunpickPointHistory sunpickPointHistory) {
        return PurchaseHistory.builder()
            .member(member)
            .coupon(coupon)
            .exclusiveSaleEvent(exclusiveSaleEvent)
            .sunpickPointHistory(sunpickPointHistory)
            .price(20000)
            .purchaseType(PurchaseType.CARD)
            .build();
    }

}
