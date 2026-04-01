package org.gustas.inventory.inventorymanagementsystem.domain.company.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.domain.company.dto.CompanyDto;
import org.gustas.inventory.inventorymanagementsystem.domain.company.dto.SaveCompanyDto;
import org.gustas.inventory.inventorymanagementsystem.domain.company.service.CompanyService;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseDto;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public PagedResponseDto<CompanyDto> getCompanies(@RequestParam(name = "page", defaultValue = "1") Integer page) {
        return companyService.getCompanies(page);
    }

    @PostMapping
    public CompanyDto saveCompany(@Valid @RequestBody SaveCompanyDto saveCompanyDto) {
        return companyService.saveCompany(saveCompanyDto);
    }

    @PutMapping("/{companyId}")
    public CompanyDto updateCompany(@PathVariable UUID companyId, @Valid @RequestBody SaveCompanyDto saveCompanyDto) {
        return companyService.updateCompany(companyId, saveCompanyDto);
    }

    @PatchMapping("/{companyId}/disable")
    public CompanyDto disableCompany(@PathVariable UUID companyId) {
        return companyService.disableCompany(companyId);
    }

    @PatchMapping("/{companyId}/enable")
    public CompanyDto enableCompany(@PathVariable UUID companyId) {
        return companyService.enableCompany(companyId);
    }

}
