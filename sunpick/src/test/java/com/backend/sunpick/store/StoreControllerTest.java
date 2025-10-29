package com.backend.sunpick.store;

import com.backend.sunpick.domain.store.dto.request.StoreModifyRequest;
import com.backend.sunpick.domain.store.dto.response.StoreResponse;
import com.backend.sunpick.global.config.TestSecurityConfig;
import com.backend.sunpick.domain.store.controller.StoreController;
import com.backend.sunpick.domain.store.dto.request.StoreCreateRequest;
import com.backend.sunpick.domain.store.service.StoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreController.class)
@Import(TestSecurityConfig.class)
public class StoreControllerTest {

    @MockitoBean
    private StoreService storeService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/store - 상점 등록 201 Created")
    void createStore_success() throws Exception {

        StoreCreateRequest request = new StoreCreateRequest(1, "storeName", "description");

        mockMvc.perform(post("/api/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
        Mockito.verify(storeService, Mockito.times(1)).createStore(any(StoreCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/store - 상점 등록 memberId가 null이면 400 Bad Request")
    void createStore_fail_memberIdNull() throws Exception {
        StoreCreateRequest request = new StoreCreateRequest(null, "storeName", "description");

        mockMvc.perform(post("/api/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        Mockito.verify(storeService, Mockito.never()).createStore(any());
    }

    @Test
    @DisplayName("POST /api/store - 상점 등록 memberId가 0이면 400 Bad Request")
    void createStore_fail_memberIdZero() throws Exception {
        StoreCreateRequest request = new StoreCreateRequest(0, "storeName", "description");

        mockMvc.perform(post("/api/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        Mockito.verify(storeService, Mockito.never()).createStore(any());
    }

    @Test
    @DisplayName("POST /api/store - 상점 등록 name이 비어있으면 400 Bad Request")
    void createStore_fail_nameBlank() throws Exception {
        StoreCreateRequest request = new StoreCreateRequest(1, "", "description");

        mockMvc.perform(post("/api/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        Mockito.verify(storeService, Mockito.never()).createStore(any());
    }

    @Test
    @DisplayName("POST /api/store - 상점 등록 name이 20자 초과면 400 Bad Request")
    void createStore_fail_nameTooLong() throws Exception {
        String longName = "a".repeat(21);
        StoreCreateRequest request = new StoreCreateRequest(1, longName, "description");

        mockMvc.perform(post("/api/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        Mockito.verify(storeService, Mockito.never()).createStore(any());
    }

    @Test
    @DisplayName("GET /api/store - 상점 목록 조회 200 OK")
    void getStoreAll_success() throws Exception {
        List<StoreResponse> responses = List.of(
            new StoreResponse(1, "store1", "description1", "owner1"),
            new StoreResponse(2, "store2", "description2", "owner2")
        );
        when(storeService.getStoreAll()).thenReturn(responses);

        mockMvc.perform(get("/api/store"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("store1"))
            .andExpect(jsonPath("$[0].description").value("description1"))
            .andExpect(jsonPath("$[0].ownerName").value("owner1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("store2"))
            .andExpect(jsonPath("$[1].description").value("description2"))
            .andExpect(jsonPath("$[1].ownerName").value("owner2"));

        Mockito.verify(storeService, Mockito.times(1)).getStoreAll();
    }

    @Test
    @DisplayName("GET /api/store/{storeId} - 상점 단건 조회 200 OK")
    void getStoreById_success() throws Exception {
        int storeId = 1;
        StoreResponse response = new StoreResponse(storeId, "store", "description", "owner");

        when(storeService.getStoreById(storeId)).thenReturn(response);

        mockMvc.perform(get("/api/store/{storeId}", storeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(storeId))
            .andExpect(jsonPath("$.name").value("store"))
            .andExpect(jsonPath("$.description").value("description"))
            .andExpect(jsonPath("$.ownerName").value("owner"));

        Mockito.verify(storeService, Mockito.times(1)).getStoreById(storeId);
    }

    @Test
    @DisplayName("PATCH /api/store/{id} - 상점 수정 204 No Content")
    void modifyStore_success() throws Exception {
        StoreModifyRequest request = new StoreModifyRequest(1, "new store", "new description");

        mockMvc.perform(patch("/api/store/{storeId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNoContent());

        Mockito.verify(storeService, Mockito.times(1))
            .modifyStore(any(Integer.class), any(StoreModifyRequest.class));
    }

    @Test
    @DisplayName("POST /api/store - 상점 수정 memberId가 0이면 400 Bad Request")
    void modifyStore_fail_memberIdZero() throws Exception {
        StoreModifyRequest request = new StoreModifyRequest(0, "storeName", "description");

        mockMvc.perform(patch("/api/store/{storeId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        Mockito.verify(storeService, Mockito.never())
            .modifyStore(any(Integer.class), any(StoreModifyRequest.class));
    }

    @Test
    @DisplayName("PATCH /api/store/{id} - 상점 수정 name이 20자 초과면 400 Bad Request")
    void modifyStore_fail_nameTooLong() throws Exception {
        String longName = "a".repeat(21);
        StoreModifyRequest request = new StoreModifyRequest(1, longName, "description");

        mockMvc.perform(patch("/api/store/{storeId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        Mockito.verify(storeService, Mockito.never())
            .modifyStore(any(Integer.class), any(StoreModifyRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/store/{id} - 상점 삭제 204 No Content")
    void deleteStore_success() throws Exception {
        mockMvc.perform(delete("/api/store/{storeId}", 1))
            .andExpect(status().isNoContent());

        Mockito.verify(storeService, Mockito.times(1)).deleteStore(any(Integer.class));
    }
}
