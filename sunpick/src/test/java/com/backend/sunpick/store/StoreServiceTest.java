package com.backend.sunpick.store;

import com.backend.sunpick.domain.member.entity.Member;
import com.backend.sunpick.domain.member.repository.MemberRepository;
import com.backend.sunpick.domain.store.dto.request.StoreCreateRequest;
import com.backend.sunpick.domain.store.dto.request.StoreModifyRequest;
import com.backend.sunpick.domain.store.dto.response.StoreResponse;
import com.backend.sunpick.domain.store.entity.Store;
import com.backend.sunpick.domain.store.repository.StoreRepository;
import com.backend.sunpick.domain.store.service.StoreService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private StoreService storeService;

    private static final int NON_EXISTENT_ID = Integer.MAX_VALUE;

    @Test
    @DisplayName("createStore() - 성공")
    void createStore_success() {
        StoreCreateRequest request = new StoreCreateRequest(1, "storeName", "description");
        Member member = mock(Member.class);
        when(member.getName()).thenReturn("memberName");
        when(memberRepository.findById(1)).thenReturn(Optional.of(member));

        storeService.createStore(request);

        ArgumentCaptor<Store> captor = ArgumentCaptor.forClass(Store.class);
        verify(storeRepository, times(1)).save(captor.capture());
        Store saved = captor.getValue();

        assertThat(saved.getName()).isEqualTo("storeName");
        assertThat(saved.getDescription()).isEqualTo("description");
        assertThat(saved.getMember()).isEqualTo(member);
        assertThat(saved.getOwnerName()).isEqualTo("memberName");
        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    @DisplayName("createStore() - 실패 memberId가 존재하지 않는 경우")
    void createStore_fail_memberNotFound() {
        StoreCreateRequest request = new StoreCreateRequest(NON_EXISTENT_ID, "storeName",
            "description");
        when(memberRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
            () -> storeService.createStore(request));
        assertEquals("회원 ID: " + NON_EXISTENT_ID + "가 존재하지 않습니다.", exception.getMessage());
        verify(storeRepository, never()).save(any(Store.class));
    }

    @Test
    @DisplayName("modifyStore() - 성공")
    void modifyStore_success() {
        Member oldOwner = mock(Member.class);
        Store store = Store.builder()
            .name("name")
            .description("description")
            .ownerName("ownerName")
            .member(oldOwner)
            .build();
        when(storeRepository.findById(1)).thenReturn(Optional.of(store));

        Member newOwner = mock(Member.class);
        when(newOwner.getName()).thenReturn("newOwnerName");
        when(memberRepository.findById(2)).thenReturn(Optional.of(newOwner));

        StoreModifyRequest request = new StoreModifyRequest(2, "newName", "newDescription");

        storeService.modifyStore(1, request);

        assertEquals("newName", store.getName());
        assertEquals("newDescription", store.getDescription());
        assertEquals(newOwner, store.getMember());
        assertEquals("newOwnerName", store.getOwnerName());
    }

    @Test
    @DisplayName("modifyStore() - 실패 storeId가 존재하지 않는 경우")
    void modifyStore_fail_storeNotFound() {
        StoreModifyRequest request = new StoreModifyRequest(1, "newName", "newDescription");
        when(storeRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
            () -> storeService.modifyStore(NON_EXISTENT_ID, request));

        assertEquals("상점 ID: " + NON_EXISTENT_ID + "가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("modifyStore() - 실패 storeId가 삭제된 경우")
    void modifyStore_fail_deletedStore() {
        Store store = mock(Store.class);
        when(store.isDeleted()).thenReturn(true);
        when(storeRepository.findById(1)).thenReturn(Optional.of(store));
        StoreModifyRequest request = new StoreModifyRequest(1, "newName", "newDescription");

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
            () -> storeService.modifyStore(1, request));
        assertEquals("상점 ID: " + 1 + "는 삭제되었습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("modifyStore() - 실패 memberId가 존재하지 않는 경우")
    void modifyStore_fail_memberNotFound() {
        Store store = mock(Store.class);
        when(store.isDeleted()).thenReturn(false);
        when(storeRepository.findById(1)).thenReturn(Optional.of(store));
        when(memberRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        StoreModifyRequest request = new StoreModifyRequest(NON_EXISTENT_ID, "newName",
            "newDescription");

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
            () -> storeService.modifyStore(1, request));
        assertEquals("회원 ID: " + NON_EXISTENT_ID + "가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("deleteStore() - 성공")
    void deleteStore_success() {
        Store store = Store.builder()
            .name("name")
            .description("description")
            .ownerName("ownerName")
            .build();
        when(storeRepository.findById(1)).thenReturn(Optional.of(store));

        storeService.deleteStore(1);

        assertTrue(store.isDeleted());
    }

    @Test
    @DisplayName("deleteStore() - 실패 storeId가 존재하지 않는 경우")
    void deleteStore_fail_storeNotFound() {
        when(storeRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
            () -> storeService.deleteStore(NON_EXISTENT_ID));

        assertEquals("상점 ID: " + NON_EXISTENT_ID + "가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("getStoreAll() - 성공")
    void getStoreAll_success() {
        Member member1 = mock(Member.class);
        when(member1.getId()).thenReturn(1);

        Store store1 = mock(Store.class);
        when(store1.getId()).thenReturn(1);
        when(store1.getName()).thenReturn("store1");
        when(store1.getDescription()).thenReturn("description1");
        when(store1.getMember()).thenReturn(member1);
        when(store1.getOwnerName()).thenReturn("ownerName1");

        Member member2 = mock(Member.class);
        when(member2.getId()).thenReturn(2);

        Store store2 = mock(Store.class);
        when(store2.getId()).thenReturn(2);
        when(store2.getName()).thenReturn("store2");
        when(store2.getDescription()).thenReturn("description2");
        when(store2.getMember()).thenReturn(member2);
        when(store2.getOwnerName()).thenReturn("ownerName2");

        when(storeRepository.findAll()).thenReturn(List.of(store1, store2));

        List<StoreResponse> response = storeService.getStoreAll();

        assertEquals(2, response.size());

        assertEquals(1, response.get(0).id());
        assertEquals("store1", response.get(0).name());
        assertEquals("description1", response.get(0).description());
        assertEquals(1, response.get(0).ownerId());
        assertEquals("ownerName1", response.get(0).ownerName());

        assertEquals(2, response.get(1).id());
        assertEquals("store2", response.get(1).name());
        assertEquals("description2", response.get(1).description());
        assertEquals(2, response.get(1).ownerId());
        assertEquals("ownerName2", response.get(1).ownerName());
    }

    @Test
    @DisplayName("getStoreById() - 성공")
    void getStoreById_success() {
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(1);

        Store store = mock(Store.class);
        when(store.getId()).thenReturn(1);
        when(store.getName()).thenReturn("store");
        when(store.getDescription()).thenReturn("description");
        when(store.getMember()).thenReturn(member);
        when(store.getOwnerName()).thenReturn("ownerName");

        when(storeRepository.findById(1)).thenReturn(Optional.of(store));

        StoreResponse response = storeService.getStoreById(1);

        assertEquals(1, response.id());
        assertEquals("store", response.name());
        assertEquals("description", response.description());
        assertEquals(1, response.ownerId());
        assertEquals("ownerName", response.ownerName());
    }

    @Test
    @DisplayName("getStoreById() - 실패 storeId가 존재하지 않는 경우")
    void getStoreById_fail_storeNotFound() {
        when(storeRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
            () -> storeService.getStoreById(NON_EXISTENT_ID));

        assertEquals("상점 ID: " + NON_EXISTENT_ID + "가 존재하지 않습니다.", exception.getMessage());
    }
}
