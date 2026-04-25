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
public class AiItemSuggestionDto {

    private String scopeLabel;
    private String imageSummary;
    private List<AiInventoryOptionDto> inventories;
    private AiSuggestedItemDto item;

}
