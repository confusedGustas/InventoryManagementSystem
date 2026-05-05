package org.gustas.inventory.inventorymanagementsystem.domain.apikey.service;

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
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private CurrentUserContext currentUserContext;
    @Mock
    private AuthUtils authUtils;
    @Mock
    private CompanyIntegrity companyIntegrity;
    @Mock
    private ApiKeyRepository apiKeyRepository;
    @Mock
    private ApiKeyMapper apiKeyMapper;

    @InjectMocks
    private ApiKeyService apiKeyService;

    @Test
    void shouldReturnEmptyDtoWhenApiKeyMissing() {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(authUtils.isCompanyAdmin(user)).thenReturn(true);
        when(apiKeyRepository.findByCompanyId(company.getId())).thenReturn(null);

        ApiKeyDto result = apiKeyService.getCurrentApiKey("manager");

        assertThat(result.getCompanyId()).isEqualTo(company.getId());
        assertThat(result.getApiKey()).isEmpty();
    }

    @Test
    void shouldUpdateExistingApiKey() {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        ApiKey apiKey = ApiKey.builder().apiKey("old").company(company).build();
        ApiKeyDto dto = ApiKeyDto.builder().apiKey("new-secret").build();
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(authUtils.isCompanyAdmin(user)).thenReturn(true);
        when(apiKeyRepository.findByCompanyId(company.getId())).thenReturn(apiKey);
        when(apiKeyRepository.save(apiKey)).thenReturn(apiKey);
        when(apiKeyMapper.toDto(apiKey)).thenReturn(dto);

        ApiKeyDto result = apiKeyService.saveCurrentApiKey("manager", new SaveApiKeyDto("  new-secret  "));

        assertThat(result.getApiKey()).isEqualTo("new-secret");
        verify(apiKeyMapper).update(apiKey, "new-secret");
    }

    @Test
    void shouldCreateNewApiKeyWhenMissing() {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        ApiKey apiKey = ApiKey.builder().apiKey("new-secret").company(company).build();
        ApiKeyDto dto = ApiKeyDto.builder().apiKey("new-secret").companyId(company.getId()).build();
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(authUtils.isCompanyAdmin(user)).thenReturn(true);
        when(apiKeyRepository.findByCompanyId(company.getId())).thenReturn(null);
        when(apiKeyMapper.toEntity("new-secret", company)).thenReturn(apiKey);
        when(apiKeyRepository.save(apiKey)).thenReturn(apiKey);
        when(apiKeyMapper.toDto(apiKey)).thenReturn(dto);

        ApiKeyDto result = apiKeyService.saveCurrentApiKey("manager", new SaveApiKeyDto("  new-secret  "));

        assertThat(result.getApiKey()).isEqualTo("new-secret");
        verify(apiKeyMapper).toEntity("new-secret", company);
    }

    @Test
    void shouldRejectNonAdminUser() {
        User user = TestDataFactory.companyUser(TestDataFactory.company());
        when(currentUserContext.getCurrentUser("employee")).thenReturn(user);
        when(authUtils.isCompanyAdmin(user)).thenReturn(false);

        assertThatThrownBy(() -> apiKeyService.getCurrentApiKey("employee"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Only company admins and platform admins can manage AI API keys");
    }

    @Test
    void shouldAllowPlatformAdminToManageOwnApiKey() {
        User user = TestDataFactory.platformAdmin();
        ApiKey apiKey = ApiKey.builder().apiKey("platform-secret").user(user).build();
        ApiKeyDto dto = ApiKeyDto.builder().apiKey("platform-secret").companyId(null).build();
        when(currentUserContext.getCurrentUser("admin")).thenReturn(user);
        when(authUtils.isPlatformAdmin(user)).thenReturn(true);
        when(apiKeyRepository.findByUserId(user.getId())).thenReturn(apiKey);
        when(apiKeyRepository.save(apiKey)).thenReturn(apiKey);
        when(apiKeyMapper.toDto(apiKey)).thenReturn(dto);

        ApiKeyDto result = apiKeyService.saveCurrentApiKey("admin", new SaveApiKeyDto("platform-secret"));

        assertThat(result.getCompanyId()).isNull();
        verify(apiKeyMapper).update(apiKey, "platform-secret");
    }

    @Test
    void shouldReturnEmptyDtoWhenPlatformAdminApiKeyMissing() {
        User user = TestDataFactory.platformAdmin();
        when(currentUserContext.getCurrentUser("admin")).thenReturn(user);
        when(authUtils.isPlatformAdmin(user)).thenReturn(true);
        when(apiKeyRepository.findByUserId(user.getId())).thenReturn(null);

        ApiKeyDto result = apiKeyService.getCurrentApiKey("admin");

        assertThat(result.getApiKey()).isEmpty();
        assertThat(result.getCompanyId()).isNull();
    }

    @Test
    void shouldCreatePlatformAdminApiKeyWhenMissing() {
        User user = TestDataFactory.platformAdmin();
        ApiKey apiKey = ApiKey.builder().apiKey("platform-secret").user(user).build();
        ApiKeyDto dto = ApiKeyDto.builder().apiKey("platform-secret").build();
        when(currentUserContext.getCurrentUser("admin")).thenReturn(user);
        when(authUtils.isPlatformAdmin(user)).thenReturn(true);
        when(apiKeyRepository.findByUserId(user.getId())).thenReturn(null);
        when(apiKeyMapper.toEntity("platform-secret", user)).thenReturn(apiKey);
        when(apiKeyRepository.save(apiKey)).thenReturn(apiKey);
        when(apiKeyMapper.toDto(apiKey)).thenReturn(dto);

        ApiKeyDto result = apiKeyService.saveCurrentApiKey("admin", new SaveApiKeyDto(" platform-secret "));

        assertThat(result.getApiKey()).isEqualTo("platform-secret");
        verify(apiKeyMapper).toEntity("platform-secret", user);
    }
}
