package org.gustas.inventory.inventorymanagementsystem.domain.apikey.mapper;

import org.gustas.inventory.inventorymanagementsystem.domain.apikey.dto.ApiKeyDto;
import org.gustas.inventory.inventorymanagementsystem.domain.apikey.entity.ApiKey;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyMapper {

    public ApiKeyDto toDto(ApiKey apiKey) {
        return ApiKeyDto.builder()
                .id(apiKey.getId())
                .companyId(apiKey.getCompany() != null ? apiKey.getCompany().getId() : null)
                .companyName(apiKey.getCompany() != null ? apiKey.getCompany().getName() : null)
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

    public ApiKey toEntity(String value, User user) {
        return ApiKey.builder()
                .apiKey(value)
                .user(user)
                .build();
    }

    public void update(ApiKey apiKey, String value) {
        apiKey.setApiKey(value);
    }

}
