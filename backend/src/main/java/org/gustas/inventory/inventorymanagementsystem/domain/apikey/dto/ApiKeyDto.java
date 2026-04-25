package org.gustas.inventory.inventorymanagementsystem.domain.apikey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyDto {

    private UUID id;
    private UUID companyId;
    private String companyName;
    private String apiKey;
    private LocalDateTime updatedOn;

}
