package org.gustas.inventory.inventorymanagementsystem.domain.company.integrity;

import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.CompanyStatus;
import org.gustas.inventory.inventorymanagementsystem.domain.company.repository.CompanyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;

@Service
public class CompanyIntegrity {

    public void checkCompanyNotNull(Company company) {
        if (company == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found");
        }
    }

    public void checkCompanyIdProvided(UUID companyId) {
        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company is required for company users");
        }
    }

    public void checkCompanyNameAvailable(CompanyRepository companyRepository, String name, UUID excludedCompanyId) {
        boolean companyNameTaken = excludedCompanyId == null
                ? companyRepository.existsByNameIgnoreCase(name.trim())
                : companyRepository.existsByNameIgnoreCaseAndIdNot(name.trim(), excludedCompanyId);

        if (companyNameTaken) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Company name is already taken");
        }
    }

    public void checkCompanyCanManageLocations(Company company) {
        if (company.getStatus() == CompanyStatus.SUSPENDED || company.getStatus() == CompanyStatus.CHURNED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Locations cannot be managed for suspended or churned companies");
        }
    }

    public void checkCompanyCanManageInventories(Company company) {
        if (company.getStatus() == CompanyStatus.SUSPENDED || company.getStatus() == CompanyStatus.CHURNED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Inventories cannot be managed for suspended or churned companies");
        }
    }

    public void checkCompanyCanDisable(Company company) {
        if (company.getStatus() == CompanyStatus.SUSPENDED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Company is already suspended");
        }

        if (company.getStatus() == CompanyStatus.CHURNED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Churned companies cannot be disabled");
        }
    }

    public void checkCompanyCanEnable(Company company) {
        if (company.getStatus() == CompanyStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Company is already active");
        }

        if (company.getStatus() == CompanyStatus.CHURNED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Churned companies cannot be enabled");
        }
    }

}
