package org.gustas.inventory.inventorymanagementsystem.domain.apikey.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.domain.apikey.dto.ApiKeyDto;
import org.gustas.inventory.inventorymanagementsystem.domain.apikey.dto.SaveApiKeyDto;
import org.gustas.inventory.inventorymanagementsystem.domain.apikey.service.ApiKeyService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-keys")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COMPANY_ADMIN')")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @GetMapping("/company")
    public ApiKeyDto getCompanyApiKey(Authentication authentication) {
        return apiKeyService.getCompanyApiKey(authentication.getName());
    }

    @PutMapping("/company")
    public ApiKeyDto saveCompanyApiKey(Authentication authentication, @Valid @RequestBody SaveApiKeyDto saveApiKeyDto) {
        return apiKeyService.saveCompanyApiKey(authentication.getName(), saveApiKeyDto);
    }

}
