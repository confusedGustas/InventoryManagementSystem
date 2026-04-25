package org.gustas.inventory.inventorymanagementsystem.domain.ai.repository;

import org.gustas.inventory.inventorymanagementsystem.domain.ai.entity.AiReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AiReportRepository extends JpaRepository<AiReport, UUID> {

    List<AiReport> findTop10ByOrderByGeneratedOnDesc();

    List<AiReport> findTop10ByCompanyIdOrderByGeneratedOnDesc(UUID companyId);

    AiReport findAiReportById(UUID id);

    AiReport findAiReportByIdAndCompanyId(UUID id, UUID companyId);

}
