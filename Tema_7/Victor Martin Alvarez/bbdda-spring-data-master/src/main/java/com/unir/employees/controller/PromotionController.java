package com.unir.employees.controller;

import com.unir.employees.dao.PromotionRequest;
import com.unir.employees.dao.PromotionResponse;
import com.unir.employees.services.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@Slf4j
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    public PromotionResponse promote(@RequestBody PromotionRequest request) throws Exception {
        PromotionResponse response = promotionService.promote(request);
        return response;
    }
}
