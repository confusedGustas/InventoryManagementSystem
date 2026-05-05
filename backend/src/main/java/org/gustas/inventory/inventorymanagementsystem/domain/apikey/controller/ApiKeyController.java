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
@PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN')")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @GetMapping("/current")
    public ApiKeyDto getCurrentApiKey(Authentication authentication) {
        return apiKeyService.getCurrentApiKey(authentication.getName());
    }

    @PutMapping("/current")
    public ApiKeyDto saveCurrentApiKey(Authentication authentication, @Valid @RequestBody SaveApiKeyDto saveApiKeyDto) {
        return apiKeyService.saveCurrentApiKey(authentication.getName(), saveApiKeyDto);
    }

}
