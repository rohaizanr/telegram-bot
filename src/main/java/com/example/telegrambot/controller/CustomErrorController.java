package com.example.telegrambot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom error controller for handling errors gracefully.
 */
@RestController
@Slf4j
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String errorMessage = (String) request.getAttribute("jakarta.servlet.error.message");
        String requestUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");
        
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", statusCode != null ? statusCode : HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", HttpStatus.valueOf(statusCode != null ? statusCode : 500).getReasonPhrase());
        errorResponse.put("message", errorMessage != null ? errorMessage : "An unexpected error occurred");
        errorResponse.put("path", requestUri);
        
        log.error("Error occurred: Status={}, Path={}, Message={}", statusCode, requestUri, errorMessage);
        
        return ResponseEntity.status(statusCode != null ? statusCode : 500).body(errorResponse);
    }
}
