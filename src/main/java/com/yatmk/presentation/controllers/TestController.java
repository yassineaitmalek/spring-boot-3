package com.yatmk.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yatmk.persistence.presentation.ApiDataResponse;
import com.yatmk.presentation.config.AbstractController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController implements AbstractController {

    @GetMapping
    public ResponseEntity<ApiDataResponse<String>> getMethodName() {
        return ok(() -> "hiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
    }

}
