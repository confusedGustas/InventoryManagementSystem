package org.gustas.inventory.inventorymanagementsystem.domain.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisDto {

    private UUID reportId;
    private CompanyOptionDto selectedCompany;
    private List<CompanyOptionDto> companies;
    private String scopeLabel;
    private String overview;
    private List<AiAnalysisItemDto> depletionRisks;
    private List<AiAnalysisItemDto> oversupplyRisks;
    private List<AiAnalysisItemDto> suggestedPurchases;
    private List<String> actions;
    private LocalDateTime generatedOn;

}
