package org.gustas.inventory.inventorymanagementsystem.domain.location.repository;

import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {

    boolean existsByCompanyIdAndNameIgnoreCase(UUID companyId, String name);
    boolean existsByCompanyIdAndNameIgnoreCaseAndIdNot(UUID companyId, String name, UUID id);
    Location findLocationById(UUID id);
    Location findLocationByIdAndCompanyId(UUID id, UUID companyId);
    List<Location> findAllByCompanyId(UUID companyId);
    Page<Location> findAllByCompanyId(UUID companyId, Pageable pageable);

}
