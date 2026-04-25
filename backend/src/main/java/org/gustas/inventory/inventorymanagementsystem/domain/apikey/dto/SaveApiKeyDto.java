package org.gustas.inventory.inventorymanagementsystem.domain.apikey.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveApiKeyDto {

    @NotBlank
    @Size(max = 4000)
    private String apiKey;

}
