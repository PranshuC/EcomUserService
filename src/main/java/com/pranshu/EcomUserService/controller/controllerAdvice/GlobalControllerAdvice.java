package com.pranshu.EcomUserService.controller.controllerAdvice;

import com.pranshu.EcomUserService.dto.ErrorResponseDTO;
import com.pranshu.EcomUserService.exception.InvalidTokenException;
import com.pranshu.EcomUserService.exception.TokenExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(value = {InvalidTokenException.class, TokenExpiredException.class})
    public ResponseEntity<ErrorResponseDTO> handleTokenExceptions(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setStatusCode(403);
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}
