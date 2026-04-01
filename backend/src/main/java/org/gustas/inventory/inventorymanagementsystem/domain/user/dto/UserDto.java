package org.gustas.inventory.inventorymanagementsystem.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private UUID id;
    private String userRole;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Locale locale;
    private boolean enabled;
    private UUID companyId;
    private String companyName;
    private LocalDateTime createdOn;

}
