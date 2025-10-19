package com.backend.sunpick.domain.store.service;

import com.backend.sunpick.domain.member.entity.Member;
import com.backend.sunpick.domain.member.repository.MemberRepository;
import com.backend.sunpick.domain.store.dto.request.StoreCreateRequest;
import com.backend.sunpick.domain.store.entity.Store;
import com.backend.sunpick.domain.store.repository.StoreRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    public void createStore(StoreCreateRequest storeCreateRequest) {
        Member member =  memberRepository.findById(storeCreateRequest.getMemberId())
            .orElseThrow(() -> new NoSuchElementException("회원 ID: " + storeCreateRequest.getMemberId() + "가 존재하지 안습니다."));
        storeRepository.save(Store.builder()
                .member(member)
                .name(storeCreateRequest.getStoreName())
                .description(storeCreateRequest.getDescription())
                .ownerName(member.getName())
            .build());
    }
}
