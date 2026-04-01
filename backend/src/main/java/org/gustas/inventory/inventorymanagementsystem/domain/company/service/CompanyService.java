package org.gustas.inventory.inventorymanagementsystem.domain.company.service;

import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.common.Constants;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseDto;
import org.gustas.inventory.inventorymanagementsystem.domain.company.dto.CompanyDto;
import org.gustas.inventory.inventorymanagementsystem.domain.company.dto.SaveCompanyDto;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.CompanyStatus;
import org.gustas.inventory.inventorymanagementsystem.domain.company.integrity.CompanyIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.company.mapper.CompanyMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.company.repository.CompanyRepository;
import org.gustas.inventory.inventorymanagementsystem.common.mapper.PagedResponseMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyIntegrity companyIntegrity;
    private final CompanyMapper companyMapper;
    private final PagedResponseMapper pagedResponseMapper;

    @Transactional(readOnly = true)
    public PagedResponseDto<CompanyDto> getCompanies(Integer page) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), Constants.DEFAULT_PAGE_SIZE);

        Page<CompanyDto> companyPage = companyRepository.findAll(pageable)
                .map(companyMapper::toDto);

        return pagedResponseMapper.toDto(companyPage);
    }

    @Transactional
    public CompanyDto saveCompany(SaveCompanyDto saveCompanyDto) {
        companyIntegrity.checkCompanyNameAvailable(companyRepository, saveCompanyDto.getName(), null);

        Company company = companyMapper.toCompany(saveCompanyDto);
        Company savedCompany = companyRepository.save(company);

        return companyMapper.toDto(savedCompany);
    }

    @Transactional
    public CompanyDto updateCompany(UUID companyId, SaveCompanyDto saveCompanyDto) {
        Company company = companyRepository.findCompanyById(companyId);

        companyIntegrity.checkCompanyNotNull(company);
        companyIntegrity.checkCompanyNameAvailable(companyRepository, saveCompanyDto.getName(), company.getId());

        companyMapper.updateCompany(company, saveCompanyDto);

        return companyMapper.toDto(companyRepository.save(company));
    }

    @Transactional
    public CompanyDto disableCompany(UUID companyId) {
        Company company = companyRepository.findCompanyById(companyId);

        companyIntegrity.checkCompanyNotNull(company);
        companyIntegrity.checkCompanyCanDisable(company);
        company.setStatus(CompanyStatus.SUSPENDED);

        return companyMapper.toDto(companyRepository.save(company));
    }

    @Transactional
    public CompanyDto enableCompany(UUID companyId) {
        Company company = companyRepository.findCompanyById(companyId);

        companyIntegrity.checkCompanyNotNull(company);
        companyIntegrity.checkCompanyCanEnable(company);
        company.setStatus(CompanyStatus.ACTIVE);

        return companyMapper.toDto(companyRepository.save(company));
    }

}
