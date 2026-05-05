package org.gustas.inventory.inventorymanagementsystem.domain.user.mapper;

import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.UserRole;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void shouldNormalizeUserOnCreate() {
        Company company = TestDataFactory.company();

        User result = userMapper.toUser(TestDataFactory.createUserDto(company.getId()), "encoded", company);

        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getEmail()).isEqualTo("jane@example.com");
        assertThat(result.getUserRole()).isEqualTo(UserRole.COMPANY_USER);
        assertThat(result.getLocale()).isEqualTo(Locale.ENGLISH);
    }
}
