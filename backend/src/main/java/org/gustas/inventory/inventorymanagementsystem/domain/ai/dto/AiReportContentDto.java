package org.gustas.inventory.inventorymanagementsystem.domain.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiReportContentDto {

    private String scopeLabel;
    private String overview;
    private List<AiAnalysisItemDto> depletionRisks;
    private List<AiAnalysisItemDto> oversupplyRisks;
    private List<AiAnalysisItemDto> suggestedPurchases;
    private List<String> actions;

}
