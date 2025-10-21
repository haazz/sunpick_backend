package com.backend.sunpick.domain.store.controller;

import com.backend.sunpick.domain.store.dto.request.StoreCreateRequest;
import com.backend.sunpick.domain.store.dto.response.StoreResponse;
import com.backend.sunpick.domain.store.service.StoreService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<Void> createStore(@RequestBody @Valid StoreCreateRequest request) {
        storeService.createStore(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<StoreResponse>> getStoreAll() {
        List<StoreResponse> response = storeService.getStoreAll();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Void> getStore() {
        return null;
    }

    @PatchMapping("{id}")
    public ResponseEntity<Void> modifyStore() {
        return null;
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStore() {
        return null;
    }
}
