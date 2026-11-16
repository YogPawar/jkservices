package com.jkservice.farmer.dto;

import java.time.LocalDateTime;

public record FarmerResponse(
    Long id,
    String farmerCode,
    String name,
    String phone,
    String village,
    String address,
    boolean active,
    LocalDateTime createdAt) {}
