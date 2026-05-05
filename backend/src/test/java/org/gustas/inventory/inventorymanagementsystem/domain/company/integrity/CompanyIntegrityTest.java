package org.gustas.inventory.inventorymanagementsystem.domain.company.integrity;

import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.CompanyStatus;
import org.gustas.inventory.inventorymanagementsystem.domain.company.repository.CompanyRepository;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class CompanyIntegrityTest {

    private final CompanyIntegrity companyIntegrity = new CompanyIntegrity();

    @Test
    void shouldRejectDuplicateCompanyName() {
        CompanyRepository repository = Mockito.mock(CompanyRepository.class);
        when(repository.existsByNameIgnoreCase("Acme")).thenReturn(true);

        assertThatThrownBy(() -> companyIntegrity.checkCompanyNameAvailable(repository, " Acme ", null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Company name is already taken");
    }

    @Test
    void shouldRejectSuspendedCompanyForInventoryManagement() {
        Company company = TestDataFactory.company();
        company.setStatus(CompanyStatus.SUSPENDED);

        assertThatThrownBy(() -> companyIntegrity.checkCompanyCanManageInventories(company))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Inventories cannot be managed");
    }

    @Test
    void shouldAllowActiveCompanyToEnableDisableRules() {
        Company suspendedCompany = TestDataFactory.company();
        suspendedCompany.setStatus(CompanyStatus.SUSPENDED);

        assertThatCode(() -> companyIntegrity.checkCompanyCanEnable(suspendedCompany)).doesNotThrowAnyException();
        assertThatCode(() -> companyIntegrity.checkCompanyCanDisable(TestDataFactory.company())).doesNotThrowAnyException();
    }
}
