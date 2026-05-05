package org.gustas.inventory.inventorymanagementsystem.domain.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.service.CurrentUserContext;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiAnalysisDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiAnalysisItemDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiReportContentDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiScopeDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.entity.AiReport;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.repository.AiReportRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.apikey.entity.ApiKey;
import org.gustas.inventory.inventorymanagementsystem.domain.apikey.repository.ApiKeyRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.utils.AuthUtils;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.mapper.CompanyMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.entity.Inventory;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.repository.InventoryRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.item.entity.Item;
import org.gustas.inventory.inventorymanagementsystem.domain.item.repository.ItemRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.location.repository.LocationRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private CurrentUserContext currentUserContext;
    @Mock
    private AuthUtils authUtils;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private ApiKeyRepository apiKeyRepository;
    @Mock
    private AiReportRepository aiReportRepository;
    @Mock
    private CompanyMapper companyMapper;
    @Mock
    private OpenAiAnalysisClient openAiAnalysisClient;
    @Mock
    private AiPromptProvider aiPromptProvider;

    private AiService aiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        aiService = new AiService(
                currentUserContext,
                authUtils,
                inventoryRepository,
                itemRepository,
                locationRepository,
                apiKeyRepository,
                aiReportRepository,
                companyMapper,
                openAiAnalysisClient,
                objectMapper,
                aiPromptProvider
        );
    }

    @Test
    void shouldGetScopeForCompanyAdmin() {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        CompanyOptionDto option = CompanyOptionDto.builder().id(company.getId()).name("Acme").build();
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(currentUserContext.resolveManageableCompanies(eq(user), org.mockito.ArgumentMatchers.<Function<Company, CompanyOptionDto>>any()))
                .thenReturn(List.of(option));
        when(authUtils.isPlatformAdmin(user)).thenReturn(false);
        when(companyMapper.toCompanyOptionDto(company)).thenReturn(option);

        AiScopeDto result = aiService.getScope(null, "manager");

        assertThat(result.getSelectedCompany().getName()).isEqualTo("Acme");
        assertThat(result.getCompanies()).containsExactly(option);
    }

    @Test
    void shouldRejectInvalidSuggestionImage() {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(currentUserContext.resolveManageableCompanies(eq(user), org.mockito.ArgumentMatchers.<Function<Company, CompanyOptionDto>>any()))
                .thenReturn(List.of());
        when(authUtils.isPlatformAdmin(user)).thenReturn(false);
        when(inventoryRepository.findAllByCompanyId(company.getId())).thenReturn(List.of(TestDataFactory.inventory(company, null)));

        assertThatThrownBy(() -> aiService.suggestItem(null, new MockMultipartFile("image", new byte[0]), "manager"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Image is required");
    }

    @Test
    void shouldGenerateAnalysisAndPersistReport() {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        Inventory inventory = TestDataFactory.inventory(company, null);
        Item item = TestDataFactory.item(inventory);
        item.setCreatedOn(LocalDateTime.now());
        ApiKey apiKey = ApiKey.builder().apiKey("secret").company(company).build();
        CompanyOptionDto option = CompanyOptionDto.builder().id(company.getId()).name("Acme").build();
        AiAnalysisDto analysis = AiAnalysisDto.builder()
                .overview("overview")
                .depletionRisks(List.of(AiAnalysisItemDto.builder().itemName("Laptop").companyName("Acme").inventoryName("Main Inventory").locationName("Unassigned").quantity(1).reason("risk").build()))
                .oversupplyRisks(List.of())
                .suggestedPurchases(List.of())
                .actions(List.of("Action"))
                .build();
        AiReport savedReport = AiReport.builder().id(java.util.UUID.randomUUID()).company(company).scopeLabel("Acme").overview("overview").build();
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(currentUserContext.resolveManageableCompanies(eq(user), org.mockito.ArgumentMatchers.<Function<Company, CompanyOptionDto>>any()))
                .thenReturn(List.of(option));
        when(currentUserContext.resolveScopedCompanies(user, company)).thenReturn(List.of(company));
        when(authUtils.isPlatformAdmin(user)).thenReturn(false);
        when(locationRepository.findAllByCompanyId(company.getId())).thenReturn(List.of());
        when(inventoryRepository.findAllByCompanyId(company.getId())).thenReturn(List.of(inventory));
        when(itemRepository.findAllByInventoryCompanyId(company.getId())).thenReturn(List.of(item));
        when(apiKeyRepository.findByCompanyId(company.getId())).thenReturn(apiKey);
        when(aiPromptProvider.getAnalysisPromptTemplate()).thenReturn("companies:%s locations:%s inventories:%s items:%s qty:%s low:%s out:%s high:%s categories:%s empty:%s");
        when(openAiAnalysisClient.analyze(eq("secret"), any())).thenReturn(analysis);
        when(companyMapper.toCompanyOptionDto(company)).thenReturn(option);
        when(aiReportRepository.save(any())).thenReturn(savedReport);

        AiAnalysisDto result = aiService.analyze(null, "manager");

        assertThat(result.getOverview()).isEqualTo("overview");
        assertThat(result.getSelectedCompany().getName()).isEqualTo("Acme");
        assertThat(result.getReportId()).isEqualTo(savedReport.getId());
        verify(openAiAnalysisClient).analyze(eq("secret"), any());
    }

    @Test
    void shouldDownloadReportPdf() throws Exception {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        AiReport report = AiReport.builder()
                .id(java.util.UUID.randomUUID())
                .company(company)
                .scopeLabel("Acme")
                .overview("overview")
                .generatedOn(LocalDateTime.now())
                .reportJson(objectMapper.writeValueAsString(AiReportContentDto.builder()
                        .overview("overview")
                        .actions(List.of("Action"))
                        .depletionRisks(List.of())
                        .oversupplyRisks(List.of())
                        .suggestedPurchases(List.of())
                        .build()))
                .build();
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(authUtils.isPlatformAdmin(user)).thenReturn(false);
        when(currentUserContext.getManagedCompanyId(user)).thenReturn(company.getId());
        when(aiReportRepository.findAiReportByIdAndCompanyId(report.getId(), company.getId())).thenReturn(report);

        byte[] pdf = aiService.downloadReportPdf(report.getId(), "manager");

        assertThat(pdf).isNotEmpty();
    }
}
