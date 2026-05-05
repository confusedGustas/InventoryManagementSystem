package org.gustas.inventory.inventorymanagementsystem.domain.apikey.repository;

import org.gustas.inventory.inventorymanagementsystem.domain.apikey.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {

    ApiKey findByCompanyId(UUID companyId);
    ApiKey findByUserId(UUID userId);

}
