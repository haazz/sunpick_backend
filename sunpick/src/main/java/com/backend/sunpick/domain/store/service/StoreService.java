package com.backend.sunpick.domain.store.service;

import com.backend.sunpick.domain.member.entity.Member;
import com.backend.sunpick.domain.member.repository.MemberRepository;
import com.backend.sunpick.domain.store.dto.request.StoreCreateRequest;
import com.backend.sunpick.domain.store.dto.response.StoreResponse;
import com.backend.sunpick.domain.store.entity.Store;
import com.backend.sunpick.domain.store.repository.StoreRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    public void createStore(StoreCreateRequest storeCreateRequest) {
        Member member = memberRepository.findById(storeCreateRequest.memberId())
            .orElseThrow(() -> new NoSuchElementException(
                "회원 ID: " + storeCreateRequest.memberId() + "가 존재하지 안습니다."));
        storeRepository.save(Store.builder()
            .member(member)
            .name(storeCreateRequest.storeName())
            .description(storeCreateRequest.description())
            .ownerName(member.getName())
            .build());
    }

    public List<StoreResponse> getStoreAll() {
        return storeRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public StoreResponse getStoreById(Integer id) {
        Store store = storeRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("상점 ID: " + id + "가 존재하지 않습니다."));
        return toResponse(store);
    }

    private StoreResponse toResponse(Store store) {
        return new StoreResponse(store.getId(), store.getName(), store.getDescription(),
            store.getOwnerName());
    }
}
