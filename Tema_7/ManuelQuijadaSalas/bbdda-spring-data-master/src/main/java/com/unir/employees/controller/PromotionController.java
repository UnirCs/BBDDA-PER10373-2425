package com.unir.employees.controller;

import com.unir.employees.model.request.PromotionRequest;
import com.unir.employees.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
public class PromotionController {

    @Autowired
    private final PromotionService promotionsService;

    @PostMapping("/api/promotions")
    public ResponseEntity<String> promote(@RequestBody PromotionRequest promotionRequest) throws ParseException {
        return ResponseEntity.ok(promotionsService.promote(promotionRequest));
    }
}
