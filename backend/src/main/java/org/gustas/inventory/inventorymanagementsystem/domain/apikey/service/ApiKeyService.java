package org.gustas.inventory.inventorymanagementsystem.domain.apikey.service;

import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.common.service.CurrentUserContext;
import org.gustas.inventory.inventorymanagementsystem.domain.apikey.dto.ApiKeyDto;
import org.gustas.inventory.inventorymanagementsystem.domain.apikey.dto.SaveApiKeyDto;
import org.gustas.inventory.inventorymanagementsystem.domain.apikey.entity.ApiKey;
import org.gustas.inventory.inventorymanagementsystem.domain.apikey.mapper.ApiKeyMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.apikey.repository.ApiKeyRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.utils.AuthUtils;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.integrity.CompanyIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final CurrentUserContext currentUserContext;
    private final AuthUtils authUtils;
    private final CompanyIntegrity companyIntegrity;
    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyMapper apiKeyMapper;

    @Transactional(readOnly = true)
    public ApiKeyDto getCurrentApiKey(String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        if (authUtils.isPlatformAdmin(currentUser)) {
            ApiKey apiKey = apiKeyRepository.findByUserId(currentUser.getId());
            if (apiKey == null) {
                return ApiKeyDto.builder()
                        .id(null)
                        .companyId(null)
                        .companyName(null)
                        .apiKey("")
                        .updatedOn(null)
                        .build();
            }

            return apiKeyMapper.toDto(apiKey);
        }

        checkCompanyAdmin(currentUser);
        Company company = currentUser.getCompany();
        companyIntegrity.checkCompanyNotNull(company);

        ApiKey apiKey = apiKeyRepository.findByCompanyId(company.getId());
        if (apiKey == null) {
            return ApiKeyDto.builder()
                    .id(null)
                    .companyId(company.getId())
                    .companyName(company.getName())
                    .apiKey("")
                    .updatedOn(null)
                    .build();
        }

        return apiKeyMapper.toDto(apiKey);
    }

    @Transactional
    public ApiKeyDto saveCurrentApiKey(String username, SaveApiKeyDto saveApiKeyDto) {
        User currentUser = currentUserContext.getCurrentUser(username);
        String apiKeyValue = saveApiKeyDto.getApiKey().trim();
        if (authUtils.isPlatformAdmin(currentUser)) {
            ApiKey apiKey = apiKeyRepository.findByUserId(currentUser.getId());
            if (apiKey == null) {
                apiKey = apiKeyMapper.toEntity(apiKeyValue, currentUser);
            } else {
                apiKeyMapper.update(apiKey, apiKeyValue);
            }

            return apiKeyMapper.toDto(apiKeyRepository.save(apiKey));
        }

        checkCompanyAdmin(currentUser);
        Company company = currentUser.getCompany();
        companyIntegrity.checkCompanyNotNull(company);
        ApiKey apiKey = apiKeyRepository.findByCompanyId(company.getId());
        if (apiKey == null) {
            apiKey = apiKeyMapper.toEntity(apiKeyValue, company);
        } else {
            apiKeyMapper.update(apiKey, apiKeyValue);
        }
        return apiKeyMapper.toDto(apiKeyRepository.save(apiKey));
    }

    private void checkCompanyAdmin(User currentUser) {
        if (!authUtils.isCompanyAdmin(currentUser)) {
            throw new ResponseStatusException(FORBIDDEN, "Only company admins and platform admins can manage AI API keys");
        }
    }

}
