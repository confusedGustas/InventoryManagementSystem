package org.gustas.inventory.inventorymanagementsystem.domain.apikey.mapper;

import org.gustas.inventory.inventorymanagementsystem.domain.apikey.entity.ApiKey;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiKeyMapperTest {

    private final ApiKeyMapper apiKeyMapper = new ApiKeyMapper();

    @Test
    void shouldCreateApiKeyEntity() {
        ApiKey apiKey = apiKeyMapper.toEntity("secret", TestDataFactory.company());

        assertThat(apiKey.getApiKey()).isEqualTo("secret");
        assertThat(apiKey.getCompany().getName()).isEqualTo("Acme");
    }

    @Test
    void shouldMapAndUpdateApiKey() {
        ApiKey apiKey = ApiKey.builder()
                .id(java.util.UUID.randomUUID())
                .apiKey("secret")
                .company(TestDataFactory.company())
                .updatedOn(java.time.LocalDateTime.of(2026, 5, 4, 12, 0))
                .build();

        var dto = apiKeyMapper.toDto(apiKey);
        apiKeyMapper.update(apiKey, "new-secret");

        assertThat(dto.getCompanyName()).isEqualTo("Acme");
        assertThat(dto.getApiKey()).isEqualTo("secret");
        assertThat(apiKey.getApiKey()).isEqualTo("new-secret");
    }

    @Test
    void shouldMapUserOwnedApiKey() {
        ApiKey apiKey = ApiKey.builder()
                .id(java.util.UUID.randomUUID())
                .apiKey("secret")
                .user(TestDataFactory.platformAdmin())
                .updatedOn(java.time.LocalDateTime.of(2026, 5, 4, 12, 0))
                .build();

        var dto = apiKeyMapper.toDto(apiKey);

        assertThat(dto.getCompanyId()).isNull();
        assertThat(dto.getCompanyName()).isNull();
        assertThat(dto.getApiKey()).isEqualTo("secret");
    }
}
