package com.backend.sunpick.domain.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreCreateRequest {
    @NotNull(message = "회원 ID는 필수입니다.")
    @Positive(message = "회원 ID는 1 이상의 값이어야 합니다.")
    private Integer memberId;

    @NotBlank(message = "상점명은 필수입니다.")
    @Size(max = 20, message = "상점명은 20자 이하로 입력해 주세요.")
    private String storeName;

    @Size(max = 100, message = "상점 설명은 100자 이하로 입력해 주세요.")
    private String description;
}
