package org.gustas.inventory.inventorymanagementsystem.domain.analytics.controller;

import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsDto;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.service.AnalyticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN', 'COMPANY_USER', 'COMPANY_FINANCE')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    public AnalyticsDto getAnalytics(Authentication authentication, @RequestParam(name = "companyId", required = false) UUID companyId) {
        return analyticsService.getAnalytics(companyId, authentication.getName());
    }

}
