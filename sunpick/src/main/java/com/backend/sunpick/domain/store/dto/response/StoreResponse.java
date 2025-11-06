package com.backend.sunpick.domain.store.dto.response;

public record StoreResponse(
    Integer id,
    String name,
    String description,
    Integer ownerId,
    String ownerName
) {

}
