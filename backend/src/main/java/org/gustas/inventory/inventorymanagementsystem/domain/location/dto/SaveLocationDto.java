package org.gustas.inventory.inventorymanagementsystem.domain.location.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveLocationDto {

    @NotBlank(message = "Location name is required")
    @Size(max = 100, message = "Location name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must be at most 255 characters")
    private String address;

    @NotBlank(message = "Phone is required")
    @Size(max = 30, message = "Phone must be at most 30 characters")
    private String phone;

    @NotBlank(message = "Domain is required")
    @Size(max = 100, message = "Domain must be at most 100 characters")
    private String domain;

    @NotNull(message = "Company is required")
    private UUID companyId;

}
