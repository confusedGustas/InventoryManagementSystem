package org.gustas.inventory.inventorymanagementsystem.domain.dashboard.controller;

import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto.DashboardDto;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN', 'COMPANY_USER', 'COMPANY_FINANCE')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardDto getDashboard(
            Authentication authentication,
            @RequestParam(name = "companyId", required = false) UUID companyId
    ) {
        return dashboardService.getDashboard(companyId, authentication.getName());
    }

}
