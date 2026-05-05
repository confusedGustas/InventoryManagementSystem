package org.gustas.inventory.inventorymanagementsystem.support;

import org.gustas.inventory.inventorymanagementsystem.domain.auth.dto.LoginDto;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.dto.RegisterDto;
import org.gustas.inventory.inventorymanagementsystem.domain.company.dto.SaveCompanyDto;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.CompanyStatus;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.dto.SaveInventoryDto;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.entity.Inventory;
import org.gustas.inventory.inventorymanagementsystem.domain.item.dto.SaveItemDto;
import org.gustas.inventory.inventorymanagementsystem.domain.item.entity.Item;
import org.gustas.inventory.inventorymanagementsystem.domain.location.dto.SaveLocationDto;
import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.ChangeEmailDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.ChangePasswordDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.CreateUserDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.UpdateUserDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.UserRole;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static Company company() {
        return Company.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .name("Acme")
                .contactEmail("contact@acme.test")
                .contactPhone("+37060000000")
                .city("Vilnius")
                .postalCode("LT-01100")
                .country("Lithuania")
                .status(CompanyStatus.ACTIVE)
                .createdOn(LocalDateTime.of(2024, 1, 1, 10, 0))
                .build();
    }

    public static SaveCompanyDto saveCompanyDto() {
        return new SaveCompanyDto("  Acme  ", "  Contact@Acme.Test  ", "  +37060000000  ", "  Vilnius  ", "  LT-01100  ", "  Lithuania  ", CompanyStatus.ACTIVE);
    }

    public static Location location(Company company) {
        return Location.builder()
                .id(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .name("Warehouse")
                .address("Main St 1")
                .phone("+37061111111")
                .domain("warehouse.acme.test")
                .enabled(true)
                .createdOn(LocalDateTime.of(2024, 2, 1, 10, 0))
                .company(company)
                .build();
    }

    public static SaveLocationDto saveLocationDto(UUID companyId) {
        return new SaveLocationDto("  Warehouse  ", "  Main St 1  ", "  +37061111111  ", "  Warehouse.Acme.Test  ", companyId);
    }

    public static Inventory inventory(Company company, Location location) {
        return Inventory.builder()
                .id(UUID.fromString("33333333-3333-3333-3333-333333333333"))
                .name("Main Inventory")
                .code("INV-001")
                .description("Primary stock")
                .enabled(true)
                .createdOn(LocalDateTime.of(2024, 3, 1, 10, 0))
                .company(company)
                .location(location)
                .build();
    }

    public static SaveInventoryDto saveInventoryDto(UUID companyId, UUID locationId) {
        return new SaveInventoryDto("  Main Inventory  ", "  INV-001  ", "  Primary stock  ", companyId, locationId);
    }

    public static Item item(Inventory inventory) {
        return Item.builder()
                .id(UUID.fromString("44444444-4444-4444-4444-444444444444"))
                .name("Laptop")
                .sku("SKU-1")
                .category("Electronics")
                .brand("Lenovo")
                .manufacturer("Lenovo")
                .model("ThinkPad")
                .partNumber("PN-1")
                .serialNumber("SN-1")
                .color("Black")
                .dimensions("30x20")
                .weight("2kg")
                .unit("pcs")
                .quantity(5)
                .description("Work laptop")
                .notes("Assigned")
                .enabled(true)
                .createdOn(LocalDateTime.of(2024, 4, 1, 10, 0))
                .inventory(inventory)
                .build();
    }

    public static SaveItemDto saveItemDto(UUID inventoryId) {
        return new SaveItemDto("  Laptop  ", "  SKU-1  ", "  Electronics  ", "  Lenovo  ", "  Lenovo  ", "  ThinkPad  ", "  PN-1  ", "  SN-1  ", "  Black  ", "  30x20  ", "  2kg  ", "  pcs  ", 5, "  Work laptop  ", "  Assigned  ", inventoryId);
    }

    public static User platformAdmin() {
        return User.builder()
                .id(UUID.fromString("55555555-5555-5555-5555-555555555555"))
                .userRole(UserRole.PLATFORM_ADMIN)
                .firstName("Admin")
                .lastName("User")
                .username("admin")
                .email("admin@test.com")
                .password("encoded")
                .locale(Locale.ENGLISH)
                .enabled(true)
                .company(null)
                .createdOn(LocalDateTime.of(2024, 5, 1, 10, 0))
                .build();
    }

    public static User companyAdmin(Company company) {
        return User.builder()
                .id(UUID.fromString("66666666-6666-6666-6666-666666666666"))
                .userRole(UserRole.COMPANY_ADMIN)
                .firstName("Company")
                .lastName("Admin")
                .username("manager")
                .email("manager@acme.test")
                .password("encoded")
                .locale(Locale.ENGLISH)
                .enabled(true)
                .company(company)
                .createdOn(LocalDateTime.of(2024, 5, 2, 10, 0))
                .build();
    }

    public static User companyUser(Company company) {
        return User.builder()
                .id(UUID.fromString("77777777-7777-7777-7777-777777777777"))
                .userRole(UserRole.COMPANY_USER)
                .firstName("Company")
                .lastName("User")
                .username("employee")
                .email("employee@acme.test")
                .password("encoded")
                .locale(Locale.ENGLISH)
                .enabled(true)
                .company(company)
                .createdOn(LocalDateTime.of(2024, 5, 3, 10, 0))
                .build();
    }

    public static RegisterDto registerDto() {
        return new RegisterDto("  Jane  ", "  Doe  ", "  jane  ", "  Jane@Example.com  ", "secret");
    }

    public static LoginDto loginDto() {
        return new LoginDto("manager", "secret");
    }

    public static CreateUserDto createUserDto(UUID companyId) {
        return new CreateUserDto("  Jane  ", "  Doe  ", "  jane  ", "  Jane@Example.com  ", "secret", UserRole.COMPANY_USER, companyId);
    }

    public static UpdateUserDto updateUserDto(UUID companyId) {
        return new UpdateUserDto("  Janet  ", "  Doe  ", "  janet  ", "  Janet@Example.com  ", UserRole.COMPANY_ADMIN, companyId);
    }

    public static ChangePasswordDto changePasswordDto() {
        return new ChangePasswordDto("old-secret", "new-secret");
    }

    public static ChangeEmailDto changeEmailDto() {
        return new ChangeEmailDto("  New@Example.com  ");
    }
}
