package com.backend.sunpick.domain.store.service;

import com.backend.sunpick.domain.member.entity.Member;
import com.backend.sunpick.domain.member.repository.MemberRepository;
import com.backend.sunpick.domain.store.dto.request.StoreCreateRequest;
import com.backend.sunpick.domain.store.dto.request.StoreModifyRequest;
import com.backend.sunpick.domain.store.dto.response.StoreResponse;
import com.backend.sunpick.domain.store.entity.Store;
import com.backend.sunpick.domain.store.repository.StoreRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    public void createStore(StoreCreateRequest request) {
        Member member = memberRepository.findById(request.memberId())
            .orElseThrow(() -> new NoSuchElementException(
                "회원 ID: " + request.memberId() + "가 존재하지 안습니다."));
        storeRepository.save(Store.builder()
            .member(member)
            .name(request.storeName())
            .description(request.description())
            .ownerName(member.getName())
            .build());
    }

    public List<StoreResponse> getStoreAll() {
        return storeRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public StoreResponse getStoreById(Integer storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new NoSuchElementException("상점 ID: " + storeId + "가 존재하지 않습니다."));
        return toResponse(store);
    }

    @Transactional
    public void modifyStore(Integer storeId, StoreModifyRequest request) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new NoSuchElementException("상점 ID: " + storeId + "가 존재하지 않습니다."));

        if (request.memberId() != null) {
            Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NoSuchElementException(
                    "회원 ID: " + request.memberId() + "가 존재하지 안습니다."));
            store.changeOwner(member);
        }
        if (request.name() != null) {
            store.changeName(request.name());
        }
        if (request.description() != null) {
            store.changeDescription(request.description());
        }
    }

    private StoreResponse toResponse(Store store) {
        return new StoreResponse(store.getId(), store.getName(), store.getDescription(),
            store.getOwnerName());
    }
}
