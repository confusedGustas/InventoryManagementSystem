package org.gustas.inventory.inventorymanagementsystem.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationOptionDto {

    private UUID id;
    private String name;
    private UUID companyId;
    private String companyName;

}
