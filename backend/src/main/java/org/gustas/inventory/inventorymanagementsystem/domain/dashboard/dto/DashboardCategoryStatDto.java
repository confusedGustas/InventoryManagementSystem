package org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardCategoryStatDto {

    private String category;
    private int itemCount;
    private int totalQuantity;

}
