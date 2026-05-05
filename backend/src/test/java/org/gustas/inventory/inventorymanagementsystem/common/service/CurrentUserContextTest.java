package org.gustas.inventory.inventorymanagementsystem.common.service;

import org.gustas.inventory.inventorymanagementsystem.domain.auth.utils.AuthUtils;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.integrity.CompanyIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.company.repository.CompanyRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.domain.user.integrity.UserIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.user.repository.UserRepository;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserContextTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserIntegrity userIntegrity;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private CompanyIntegrity companyIntegrity;
    @Mock
    private AuthUtils authUtils;

    @InjectMocks
    private CurrentUserContext currentUserContext;

    @Test
    void shouldResolveCurrentUser() {
        User user = TestDataFactory.companyAdmin(TestDataFactory.company());
        when(userRepository.findByUsername("manager")).thenReturn(user);

        User result = currentUserContext.getCurrentUser("manager");

        assertThat(result).isSameAs(user);
        verify(userIntegrity).checkUserNotNull(user);
    }

    @Test
    void shouldResolveAllManageableCompaniesForPlatformAdmin() {
        Company alpha = TestDataFactory.company();
        Company beta = TestDataFactory.company();
        beta.setId(UUID.randomUUID());
        beta.setName("Beta");
        User admin = TestDataFactory.platformAdmin();
        when(authUtils.isPlatformAdmin(admin)).thenReturn(true);
        when(companyRepository.findAll()).thenReturn(List.of(beta, alpha));

        List<String> result = currentUserContext.resolveManageableCompanies(admin, Company::getName);

        assertThat(result).containsExactly("Acme", "Beta");
    }

    @Test
    void shouldUseScopedFinderForCompanyUser() {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        UUID entityId = UUID.randomUUID();
        when(authUtils.isPlatformAdmin(user)).thenReturn(false);

        String result = currentUserContext.findManagedEntity(
                user,
                entityId,
                id -> "global",
                (id, companyId) -> "scoped-" + companyId
        );

        assertThat(result).isEqualTo("scoped-" + company.getId());
    }

    @Test
    void shouldResolveExplicitCompanyForPlatformAdmin() {
        Company company = TestDataFactory.company();
        User admin = TestDataFactory.platformAdmin();
        when(authUtils.isPlatformAdmin(admin)).thenReturn(true);
        when(companyRepository.findCompanyById(company.getId())).thenReturn(company);

        Company result = currentUserContext.resolveCompany(company.getId(), admin);

        assertThat(result).isSameAs(company);
        verify(companyIntegrity).checkCompanyIdProvided(company.getId());
        verify(companyIntegrity).checkCompanyNotNull(company);
    }
}
