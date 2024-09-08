package com.pranshu.EcomUserService.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponseDTO {
    private String message;
    private int statusCode;
}
