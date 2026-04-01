package org.gustas.inventory.inventorymanagementsystem.domain.company.dto;

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
public class CompanyDto {

    private UUID id;
    private String name;
    private String contactEmail;
    private String contactPhone;
    private String city;
    private String postalCode;
    private String country;
    private String status;
    private LocalDateTime createdOn;

}
