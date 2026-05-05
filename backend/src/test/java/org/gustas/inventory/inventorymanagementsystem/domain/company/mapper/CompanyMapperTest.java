package org.gustas.inventory.inventorymanagementsystem.domain.company.mapper;

import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyMapperTest {

    private final CompanyMapper companyMapper = new CompanyMapper();

    @Test
    void shouldNormalizeCompanyWhenCreating() {
        Company result = companyMapper.toCompany(TestDataFactory.saveCompanyDto());

        assertThat(result.getName()).isEqualTo("Acme");
        assertThat(result.getContactEmail()).isEqualTo("contact@acme.test");
        assertThat(result.getCountry()).isEqualTo("Lithuania");
    }

    @Test
    void shouldUpdateCompanyInPlace() {
        Company company = TestDataFactory.company();

        companyMapper.updateCompany(company, TestDataFactory.saveCompanyDto());

        assertThat(company.getName()).isEqualTo("Acme");
        assertThat(company.getContactPhone()).isEqualTo("+37060000000");
    }
}
