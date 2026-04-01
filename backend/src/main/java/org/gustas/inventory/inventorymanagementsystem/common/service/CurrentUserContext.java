package org.gustas.inventory.inventorymanagementsystem.common.service;

import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.utils.AuthUtils;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.integrity.CompanyIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.company.repository.CompanyRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.domain.user.integrity.UserIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CurrentUserContext {

    private final UserRepository userRepository;
    private final UserIntegrity userIntegrity;
    private final CompanyRepository companyRepository;
    private final CompanyIntegrity companyIntegrity;
    private final AuthUtils authUtils;

    public User getCurrentUser(String username) {
        User user = userRepository.findByUsername(username);
        userIntegrity.checkUserNotNull(user);
        return user;
    }

    public UUID getManagedCompanyId(User currentUser) {
        Company company = currentUser.getCompany();
        companyIntegrity.checkCompanyNotNull(company);
        return company.getId();
    }

    public <T> List<T> resolveManageableCompanies(User currentUser, Function<Company, T> mapper) {
        if (authUtils.isPlatformAdmin(currentUser)) {
            return companyRepository.findAll().stream()
                    .sorted(Comparator.comparing(Company::getName, String.CASE_INSENSITIVE_ORDER))
                    .map(mapper)
                    .toList();
        }

        Company company = currentUser.getCompany();
        companyIntegrity.checkCompanyNotNull(company);
        return List.of(mapper.apply(company));
    }

    public <T> T findManagedEntity(User currentUser, UUID entityId, Function<UUID, T> globalFinder, BiFunction<UUID, UUID, T> companyScopedFinder) {
        if (authUtils.isPlatformAdmin(currentUser)) {
            return globalFinder.apply(entityId);
        }

        return companyScopedFinder.apply(entityId, getManagedCompanyId(currentUser));
    }

    public Company resolveCompany(UUID companyId, User currentUser) {
        if (authUtils.isPlatformAdmin(currentUser)) {
            companyIntegrity.checkCompanyIdProvided(companyId);
            Company company = companyRepository.findCompanyById(companyId);
            companyIntegrity.checkCompanyNotNull(company);
            return company;
        }

        Company company = currentUser.getCompany();
        companyIntegrity.checkCompanyNotNull(company);
        return company;
    }

}
