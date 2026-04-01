package org.gustas.inventory.inventorymanagementsystem.domain.company.mapper;

import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;
import org.gustas.inventory.inventorymanagementsystem.domain.company.dto.CompanyDto;
import org.gustas.inventory.inventorymanagementsystem.domain.company.dto.SaveCompanyDto;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public Company toCompany(SaveCompanyDto saveCompanyDto) {
        return Company.builder()
                .name(saveCompanyDto.getName().trim())
                .contactEmail(saveCompanyDto.getContactEmail().trim().toLowerCase())
                .contactPhone(saveCompanyDto.getContactPhone().trim())
                .city(saveCompanyDto.getCity().trim())
                .postalCode(saveCompanyDto.getPostalCode().trim())
                .country(saveCompanyDto.getCountry().trim())
                .status(saveCompanyDto.getStatus())
                .build();
    }

    public CompanyDto toDto(Company company) {
        return CompanyDto.builder()
                .id(company.getId())
                .name(company.getName())
                .contactEmail(company.getContactEmail())
                .contactPhone(company.getContactPhone())
                .city(company.getCity())
                .postalCode(company.getPostalCode())
                .country(company.getCountry())
                .status(company.getStatus().name())
                .createdOn(company.getCreatedOn())
                .build();
    }

    public CompanyOptionDto toCompanyOptionDto(Company company) {
        return CompanyOptionDto.builder()
                .id(company.getId())
                .name(company.getName())
                .build();
    }

    public void updateCompany(Company company, SaveCompanyDto saveCompanyDto) {
        company.setName(saveCompanyDto.getName().trim());
        company.setContactEmail(saveCompanyDto.getContactEmail().trim().toLowerCase());
        company.setContactPhone(saveCompanyDto.getContactPhone().trim());
        company.setCity(saveCompanyDto.getCity().trim());
        company.setPostalCode(saveCompanyDto.getPostalCode().trim());
        company.setCountry(saveCompanyDto.getCountry().trim());
        company.setStatus(saveCompanyDto.getStatus());
    }

}
