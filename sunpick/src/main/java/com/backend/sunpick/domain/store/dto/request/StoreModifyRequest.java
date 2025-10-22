package com.backend.sunpick.domain.store.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record StoreModifyRequest(
    @Positive(message = "회원 ID는 1 이상의 값이어야 합니다.")
    Integer memberId,

    @Size(max = 20, message = "상점명은 20자 이하로 입력해 주세요.")
    String name,

    @Size(max = 100, message = "상점 설명은 100자 이하로 입력해 주세요.")
    String description
) {

}
