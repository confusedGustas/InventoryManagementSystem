package org.gustas.inventory.inventorymanagementsystem.domain.company.repository;

import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
    Company findCompanyById(UUID id);

}
