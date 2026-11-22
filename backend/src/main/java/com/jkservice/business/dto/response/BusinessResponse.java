package com.jkservice.business.dto.response;

import java.time.LocalDateTime;

public record BusinessResponse(
    Long id,
    String businessName,
    String businessType,
    String businessCode,
    String contactNumber,
    String email,
    String address,
    String currencyCode,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
