package com.fooddelivery.provider.controller;

import com.fooddelivery.provider.dto.QuotationPayload;
import com.fooddelivery.provider.entity.Quotation;
import com.fooddelivery.provider.security.JwtService;
import com.fooddelivery.provider.service.QuotationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quotations")
public class QuotationController {
    private final QuotationService quotationService;
    private final JwtService jwtService;

    public QuotationController(QuotationService quotationService, JwtService jwtService) {
        this.quotationService = quotationService;
        this.jwtService = jwtService;
    }

    @GetMapping("/available")
    public Object available(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        return quotationService.availableMealRequests(jwtService.uid(auth));
    }

    @PostMapping
    public Quotation submit(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
                            @Valid @RequestBody QuotationPayload payload) {
        return quotationService.submit(jwtService.uid(auth), payload);
    }

    @GetMapping("/provider/{providerId}")
    public List<Quotation> byProvider(@PathVariable Long providerId) {
        return quotationService.byProvider(providerId);
    }

    @GetMapping("/mine")
    public List<Quotation> mine(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        return quotationService.byProvider(jwtService.uid(auth));
    }

    @GetMapping("/meal-request/{mealRequestId}")
    public List<Quotation> byMealRequest(@PathVariable Long mealRequestId) {
        return quotationService.byMealRequest(mealRequestId);
    }

    @GetMapping("/{id}")
    public Quotation get(@PathVariable Long id) {
        return quotationService.get(id);
    }

    @PutMapping("/{id}/status")
    public Quotation status(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return quotationService.updateStatus(id, body.get("status"));
    }
}
