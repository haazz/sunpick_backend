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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createStore(StoreCreateRequest request) {
        Member member = memberRepository.findById(request.memberId())
            .orElseThrow(() -> new NoSuchElementException(
                "회원 ID: " + request.memberId() + "가 존재하지 않습니다."));
        storeRepository.save(Store.builder()
            .member(member)
            .name(request.name())
            .description(request.description())
            .ownerName(member.getName())
            .build());
    }

    @Transactional(readOnly = true)
    public List<StoreResponse> getStoreAll() {
        return storeRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public StoreResponse getStoreById(Integer storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new NoSuchElementException("상점 ID: " + storeId + "가 존재하지 않습니다."));
        return toResponse(store);
    }

    @Transactional
    public void modifyStore(Integer storeId, StoreModifyRequest request) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new NoSuchElementException("상점 ID: " + storeId + "가 존재하지 않습니다."));

        if (store.isDeleted()) {
            throw new NoSuchElementException("상점 ID: " + storeId + "는 삭제되었습니다.");
        }

        Optional.ofNullable(request.memberId()).ifPresent(memberId -> {
            Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(
                    "회원 ID: " + memberId + "가 존재하지 않습니다."));
            store.changeOwner(member);
        });
        Optional.ofNullable(request.name()).ifPresent(store::changeName);
        Optional.ofNullable(request.description()).ifPresent(store::changeDescription);
    }

    @Transactional
    public void deleteStore(Integer storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new NoSuchElementException("상점 ID: " + storeId + "가 존재하지 않습니다."));
        store.delete();
    }

    private StoreResponse toResponse(Store store) {
        return new StoreResponse(store.getId(), store.getName(), store.getDescription(),
            store.getOwnerName());
    }
}
