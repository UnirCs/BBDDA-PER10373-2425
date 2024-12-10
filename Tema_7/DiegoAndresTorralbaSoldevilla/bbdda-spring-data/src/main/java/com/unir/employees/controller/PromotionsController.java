package com.unir.employees.controller;

import com.unir.employees.model.request.PromotionRequest;
import com.unir.employees.service.PromotionsService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PromotionsController {

    private final PromotionsService promotionsService;

    @PostMapping("/api/promotions")
    public ResponseEntity<String> promote(@RequestBody PromotionRequest promotionRequest) throws Exception {
        return ResponseEntity.ok(promotionsService.promote(promotionRequest));
    }
}
