package org.gustas.inventory.inventorymanagementsystem.domain.company.service;

import org.gustas.inventory.inventorymanagementsystem.common.mapper.PagedResponseMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.company.dto.CompanyDto;
import org.gustas.inventory.inventorymanagementsystem.domain.company.dto.SaveCompanyDto;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.mapper.CompanyMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.company.repository.CompanyRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.company.integrity.CompanyIntegrity;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private CompanyIntegrity companyIntegrity;
    @Mock
    private CompanyMapper companyMapper;
    @Mock
    private PagedResponseMapper pagedResponseMapper;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void shouldClampPageNumberWhenFetchingCompanies() {
        Company company = TestDataFactory.company();
        CompanyDto dto = CompanyDto.builder().name("Acme").build();
        when(companyRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(company)));
        when(companyMapper.toDto(company)).thenReturn(dto);
        when(pagedResponseMapper.toDto(any())).thenCallRealMethod();

        var result = companyService.getCompanies(0);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(companyRepository).findAll(pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
        assertThat(result.getContent()).containsExactly(dto);
    }

    @Test
    void shouldDisableCompany() {
        Company company = TestDataFactory.company();
        when(companyRepository.findCompanyById(company.getId())).thenReturn(company);
        when(companyRepository.save(company)).thenReturn(company);
        when(companyMapper.toDto(company)).thenReturn(CompanyDto.builder().status("SUSPENDED").build());

        CompanyDto result = companyService.disableCompany(company.getId());

        assertThat(company.getStatus().name()).isEqualTo("SUSPENDED");
        assertThat(result.getStatus()).isEqualTo("SUSPENDED");
    }

    @Test
    void shouldSaveCompany() {
        SaveCompanyDto saveCompanyDto = TestDataFactory.saveCompanyDto();
        Company company = TestDataFactory.company();
        CompanyDto dto = CompanyDto.builder().name("Acme").build();
        when(companyMapper.toCompany(saveCompanyDto)).thenReturn(company);
        when(companyRepository.save(company)).thenReturn(company);
        when(companyMapper.toDto(company)).thenReturn(dto);

        CompanyDto result = companyService.saveCompany(saveCompanyDto);

        assertThat(result).isSameAs(dto);
        verify(companyIntegrity).checkCompanyNameAvailable(companyRepository, saveCompanyDto.getName(), null);
        verify(companyRepository).save(company);
    }

    @Test
    void shouldUpdateCompany() {
        SaveCompanyDto saveCompanyDto = TestDataFactory.saveCompanyDto();
        Company company = TestDataFactory.company();
        CompanyDto dto = CompanyDto.builder().name("Acme").build();
        when(companyRepository.findCompanyById(company.getId())).thenReturn(company);
        when(companyRepository.save(company)).thenReturn(company);
        when(companyMapper.toDto(company)).thenReturn(dto);

        CompanyDto result = companyService.updateCompany(company.getId(), saveCompanyDto);

        assertThat(result).isSameAs(dto);
        verify(companyIntegrity).checkCompanyNotNull(company);
        verify(companyIntegrity).checkCompanyNameAvailable(companyRepository, saveCompanyDto.getName(), company.getId());
        verify(companyMapper).updateCompany(company, saveCompanyDto);
    }

    @Test
    void shouldEnableCompany() {
        Company company = TestDataFactory.company();
        company.setStatus(org.gustas.inventory.inventorymanagementsystem.domain.company.entity.CompanyStatus.SUSPENDED);
        when(companyRepository.findCompanyById(company.getId())).thenReturn(company);
        when(companyRepository.save(company)).thenReturn(company);
        when(companyMapper.toDto(company)).thenReturn(CompanyDto.builder().status("ACTIVE").build());

        CompanyDto result = companyService.enableCompany(company.getId());

        assertThat(company.getStatus().name()).isEqualTo("ACTIVE");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        verify(companyIntegrity).checkCompanyCanEnable(company);
    }
}
