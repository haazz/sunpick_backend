package com.backend.sunpick.store;

import com.backend.sunpick.global.config.TestSecurityConfig;
import com.backend.sunpick.domain.store.controller.StoreController;
import com.backend.sunpick.domain.store.dto.request.StoreCreateRequest;
import com.backend.sunpick.domain.store.service.StoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        StoreCreateRequest request = new StoreCreateRequest(0, "상점", "설명");

        mockMvc.perform(post("/api/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        Mockito.verify(storeService, Mockito.never()).createStore(any());
    }

    @Test
    @DisplayName("POST /api/store - 상점 등록 name이 비어있으면 400 Bad Request")
    void createStore_fail_nameBlank() throws Exception {
        StoreCreateRequest request = new StoreCreateRequest(1, "", "설명");

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
        StoreCreateRequest request = new StoreCreateRequest(1, longName, "설명");

        mockMvc.perform(post("/api/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        Mockito.verify(storeService, Mockito.never()).createStore(any());
    }
}
