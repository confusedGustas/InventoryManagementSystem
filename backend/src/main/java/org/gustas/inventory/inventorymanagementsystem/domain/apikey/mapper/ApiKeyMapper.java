package org.gustas.inventory.inventorymanagementsystem.domain.apikey.mapper;

import org.gustas.inventory.inventorymanagementsystem.domain.apikey.dto.ApiKeyDto;
import org.gustas.inventory.inventorymanagementsystem.domain.apikey.entity.ApiKey;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyMapper {

    public ApiKeyDto toDto(ApiKey apiKey) {
        return ApiKeyDto.builder()
                .id(apiKey.getId())
                .companyId(apiKey.getCompany().getId())
                .companyName(apiKey.getCompany().getName())
                .apiKey(apiKey.getApiKey())
                .updatedOn(apiKey.getUpdatedOn())
                .build();
    }

    public ApiKey toEntity(String value, Company company) {
        return ApiKey.builder()
                .apiKey(value)
                .company(company)
                .build();
    }

    public void update(ApiKey apiKey, String value) {
        apiKey.setApiKey(value);
    }

}
