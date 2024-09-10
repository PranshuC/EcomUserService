package com.pranshu.ecomuserservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateTokenRequestDto {
    private Long userId;
    private String token;
}

/*
Token shouldn't be a part of payload in actual systems, it should be in header
 */