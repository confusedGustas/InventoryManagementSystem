package org.gustas.inventory.inventorymanagementsystem.domain.ai.service;

import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.service.CurrentUserContext;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiAnalysisDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiAnalysisItemDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiInventoryOptionDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiReportContentDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiReportDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiItemSuggestionDto;
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
import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.gustas.inventory.inventorymanagementsystem.domain.location.repository.LocationRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiService {

    private static final int LOW_STOCK_THRESHOLD = 10;
    private static final int SHORT_LIST_LIMIT = 8;

    private final CurrentUserContext currentUserContext;
    private final AuthUtils authUtils;
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private final LocationRepository locationRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final AiReportRepository aiReportRepository;
    private final CompanyMapper companyMapper;
    private final OpenAiAnalysisClient openAiAnalysisClient;
    private final ObjectMapper objectMapper;
    private final AiPromptProvider aiPromptProvider;

    @Transactional(readOnly = true)
    public AiScopeDto getScope(UUID companyId, String username) {
        AiRequestContext context = resolveRequestContext(companyId, username);

        return AiScopeDto.builder()
                .selectedCompany(context.selectedCompany() != null ? companyMapper.toCompanyOptionDto(context.selectedCompany()) : null)
                .companies(context.companies())
                .build();
    }

    @Transactional(readOnly = true)
    public List<AiReportDto> getReports(UUID companyId, String username) {
        AiRequestContext context = resolveRequestContext(companyId, username);
        List<AiReport> reports = resolveReports(context);

        return reports.stream()
                .filter(report -> canAccessReport(context.currentUser(), report))
                .map(this::toReportDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AiItemSuggestionDto suggestItem(UUID companyId, MultipartFile image, String username) {
        AiRequestContext context = resolveRequestContext(companyId, username);
        List<Inventory> scopedInventories = resolveScopedInventories(context.selectedCompany(), context.currentUser());
        validateItemSuggestionImage(image);

        if (scopedInventories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No inventories are available for item creation");
        }

        try {
            String apiKey = resolveAnalysisApiKey(context.currentUser(), context.selectedCompany());
            AiItemSuggestionDto suggestion = openAiAnalysisClient.suggestItem(apiKey, image.getBytes(), image.getContentType());
            suggestion.setScopeLabel(context.selectedCompany() != null ? context.selectedCompany().getName() : "All companies");
            suggestion.setInventories(scopedInventories.stream()
                    .sorted(Comparator.comparing(Inventory::getName, String.CASE_INSENSITIVE_ORDER))
                    .map(this::toInventoryOptionDto)
                    .toList());
            if (suggestion.getItem() != null && suggestion.getItem().getQuantity() == null) {
                suggestion.getItem().setQuantity(1);
            }
            return suggestion;
        } catch (java.io.IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to read uploaded image");
        }
    }

    @Transactional
    public AiAnalysisDto analyze(UUID companyId, String username) {
        AiRequestContext context = resolveRequestContext(companyId, username);

        List<Company> scopedCompanies = currentUserContext.resolveScopedCompanies(context.currentUser(), context.selectedCompany());
        List<Location> scopedLocations = resolveScopedLocations(context.selectedCompany(), context.currentUser());
        List<Inventory> scopedInventories = resolveScopedInventories(context.selectedCompany(), context.currentUser());
        List<Item> scopedItems = resolveScopedItems(context.selectedCompany(), context.currentUser());

        String apiKey = resolveAnalysisApiKey(context.currentUser(), context.selectedCompany());
        String prompt = buildPrompt(scopedCompanies, scopedLocations, scopedInventories, scopedItems);
        AiAnalysisDto analysis = openAiAnalysisClient.analyze(apiKey, prompt);

        analysis.setCompanies(context.companies());
        analysis.setSelectedCompany(context.selectedCompany() != null ? companyMapper.toCompanyOptionDto(context.selectedCompany()) : null);
        analysis.setScopeLabel(context.selectedCompany() != null ? context.selectedCompany().getName() : "All companies");
        analysis.setGeneratedOn(LocalDateTime.now());
        saveReportSafely(analysis, context.currentUser(), context.selectedCompany());

        return analysis;
    }

    @Transactional(readOnly = true)
    public byte[] downloadReportPdf(UUID reportId, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        AiReport report = authUtils.isPlatformAdmin(currentUser)
                ? aiReportRepository.findAiReportById(reportId)
                : aiReportRepository.findAiReportByIdAndCompanyId(reportId, currentUserContext.getManagedCompanyId(currentUser));

        if (report == null || !canAccessReport(currentUser, report)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "AI report not found");
        }

        try {
            AiReportContentDto reportContent = objectMapper.readValue(report.getReportJson(), AiReportContentDto.class);
            return buildPdf(report, reportContent);
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read AI report");
        }
    }

    private AiRequestContext resolveRequestContext(UUID companyId, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        List<CompanyOptionDto> companies = currentUserContext.resolveManageableCompanies(currentUser, companyMapper::toCompanyOptionDto);
        Company selectedCompany = resolveSelectedCompany(currentUser, companyId);

        return new AiRequestContext(currentUser, companies, selectedCompany);
    }

    private Company resolveSelectedCompany(User currentUser, UUID companyId) {
        if (!authUtils.isPlatformAdmin(currentUser)) {
            return currentUser.getCompany();
        }

        if (companyId == null) {
            return null;
        }

        return currentUserContext.resolveCompany(companyId, currentUser);
    }

    private List<Location> resolveScopedLocations(Company selectedCompany, User currentUser) {
        if (selectedCompany != null) {
            return locationRepository.findAllByCompanyId(selectedCompany.getId());
        }

        if (authUtils.isPlatformAdmin(currentUser)) {
            return locationRepository.findAll();
        }

        return locationRepository.findAllByCompanyId(currentUserContext.getManagedCompanyId(currentUser));
    }

    private List<Inventory> resolveScopedInventories(Company selectedCompany, User currentUser) {
        if (selectedCompany != null) {
            return inventoryRepository.findAllByCompanyId(selectedCompany.getId());
        }

        if (authUtils.isPlatformAdmin(currentUser)) {
            return inventoryRepository.findAll();
        }

        return inventoryRepository.findAllByCompanyId(currentUserContext.getManagedCompanyId(currentUser));
    }

    private List<Item> resolveScopedItems(Company selectedCompany, User currentUser) {
        if (selectedCompany != null) {
            return itemRepository.findAllByInventoryCompanyId(selectedCompany.getId());
        }

        if (authUtils.isPlatformAdmin(currentUser)) {
            return itemRepository.findAll();
        }

        return itemRepository.findAllByInventoryCompanyId(currentUserContext.getManagedCompanyId(currentUser));
    }

    private String resolveAnalysisApiKey(User currentUser, Company selectedCompany) {
        if (!authUtils.isPlatformAdmin(currentUser)) {
            return getRequiredApiKey(currentUser.getCompany());
        }

        if (selectedCompany != null) {
            return getRequiredApiKey(selectedCompany);
        }

        ApiKey anyAvailableApiKey = apiKeyRepository.findAll().stream()
                .filter(apiKey -> apiKey.getApiKey() != null && !apiKey.getApiKey().isBlank())
                .max(Comparator.comparing(ApiKey::getUpdatedOn, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No company API key has been configured yet"));

        return anyAvailableApiKey.getApiKey();
    }

    private String getRequiredApiKey(Company company) {
        if (company == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No company is available for AI analysis");
        }

        ApiKey apiKey = apiKeyRepository.findByCompanyId(company.getId());
        if (apiKey == null || apiKey.getApiKey() == null || apiKey.getApiKey().isBlank()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No API key has been configured for this company");
        }

        return apiKey.getApiKey();
    }

    private String buildPrompt(List<Company> companies, List<Location> locations, List<Inventory> inventories, List<Item> items) {
        Map<UUID, List<Item>> itemsByInventoryId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getInventory().getId()));
        Map<String, List<Item>> itemsByCategory = items.stream()
                .collect(Collectors.groupingBy(item -> normalize(item.getCategory())));

        List<AiAnalysisItemDto> lowStockItems = items.stream()
                .filter(item -> quantity(item) > 0 && quantity(item) <= LOW_STOCK_THRESHOLD)
                .sorted(Comparator.comparingInt(this::quantity))
                .limit(SHORT_LIST_LIMIT)
                .map(this::toAnalysisItem)
                .toList();

        List<AiAnalysisItemDto> outOfStockItems = items.stream()
                .filter(item -> quantity(item) == 0)
                .sorted(Comparator.comparing(Item::getName, String.CASE_INSENSITIVE_ORDER))
                .limit(SHORT_LIST_LIMIT)
                .map(this::toAnalysisItem)
                .toList();

        List<AiAnalysisItemDto> highestStockItems = items.stream()
                .sorted(Comparator.comparingInt(this::quantity).reversed()
                        .thenComparing(Item::getName, String.CASE_INSENSITIVE_ORDER))
                .limit(SHORT_LIST_LIMIT)
                .map(this::toAnalysisItem)
                .toList();

        List<String> categorySummaries = itemsByCategory.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue().size() + " items / " + sumQuantity(entry.getValue()) + " units")
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        List<String> emptyInventories = inventories.stream()
                .filter(inventory -> itemsByInventoryId.getOrDefault(inventory.getId(), List.of()).isEmpty())
                .map(inventory -> inventory.getCompany().getName() + " / " + inventory.getName())
                .toList();

        return aiPromptProvider.getAnalysisPromptTemplate().formatted(
                companies.size(),
                locations.size(),
                inventories.size(),
                items.size(),
                sumQuantity(items),
                toJson(lowStockItems),
                toJson(outOfStockItems),
                toJson(highestStockItems),
                toJson(categorySummaries),
                toJson(emptyInventories)
        );
    }

    private AiAnalysisItemDto toAnalysisItem(Item item) {
        return AiAnalysisItemDto.builder()
                .itemName(item.getName())
                .companyName(item.getInventory().getCompany().getName())
                .inventoryName(item.getInventory().getName())
                .locationName(item.getInventory().getLocation() != null ? item.getInventory().getLocation().getName() : "Unassigned")
                .quantity(quantity(item))
                .reason("")
                .build();
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to prepare AI analysis context");
        }
    }

    private int quantity(Item item) {
        return item.getQuantity() != null ? item.getQuantity() : 0;
    }

    private int sumQuantity(List<Item> items) {
        return items.stream()
                .map(Item::getQuantity)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? "Uncategorized" : value;
    }

    private AiInventoryOptionDto toInventoryOptionDto(Inventory inventory) {
        return AiInventoryOptionDto.builder()
                .id(inventory.getId())
                .name(inventory.getName())
                .companyId(inventory.getCompany().getId().toString())
                .companyName(inventory.getCompany().getName())
                .locationId(inventory.getLocation() != null ? inventory.getLocation().getId().toString() : null)
                .locationName(inventory.getLocation() != null ? inventory.getLocation().getName() : null)
                .build();
    }

    private void validateItemSuggestionImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image is required");
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded file must be an image");
        }
    }

    private List<AiReport> resolveReports(AiRequestContext context) {
        if (context.selectedCompany() != null) {
            return aiReportRepository.findTop10ByCompanyIdOrderByGeneratedOnDesc(context.selectedCompany().getId());
        }

        if (authUtils.isPlatformAdmin(context.currentUser())) {
            return aiReportRepository.findTop10ByOrderByGeneratedOnDesc();
        }

        return aiReportRepository.findTop10ByCompanyIdOrderByGeneratedOnDesc(currentUserContext.getManagedCompanyId(context.currentUser()));
    }

    private AiReport saveReport(AiAnalysisDto analysis, User currentUser, Company selectedCompany) {
        try {
            AiReportContentDto reportContent = AiReportContentDto.builder()
                    .scopeLabel(analysis.getScopeLabel())
                    .overview(analysis.getOverview())
                    .depletionRisks(analysis.getDepletionRisks())
                    .oversupplyRisks(analysis.getOversupplyRisks())
                    .suggestedPurchases(analysis.getSuggestedPurchases())
                    .actions(analysis.getActions())
                    .build();

            AiReport report = AiReport.builder()
                    .scopeLabel(analysis.getScopeLabel())
                    .overview(analysis.getOverview())
                    .reportJson(objectMapper.writeValueAsString(reportContent))
                    .company(selectedCompany != null ? selectedCompany : currentUser.getCompany())
                    .generatedByUser(currentUser)
                    .build();

            return aiReportRepository.save(report);
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save AI report");
        }
    }

    private void saveReportSafely(AiAnalysisDto analysis, User currentUser, Company selectedCompany) {
        try {
            AiReport savedReport = saveReport(analysis, currentUser, selectedCompany);
            analysis.setReportId(savedReport.getId());
        } catch (RuntimeException exception) {
            log.warn("AI analysis was generated but report persistence failed", exception);
            analysis.setReportId(null);
        }
    }

    private AiReportDto toReportDto(AiReport report) {
        return AiReportDto.builder()
                .id(report.getId())
                .companyId(report.getCompany() != null ? report.getCompany().getId() : null)
                .companyName(report.getCompany() != null ? report.getCompany().getName() : null)
                .scopeLabel(report.getScopeLabel())
                .overview(report.getOverview())
                .generatedOn(report.getGeneratedOn())
                .build();
    }

    private boolean canAccessReport(User currentUser, AiReport report) {
        if (authUtils.isPlatformAdmin(currentUser)) {
            return true;
        }

        return report.getCompany() != null
                && currentUser.getCompany() != null
                && report.getCompany().getId().equals(currentUser.getCompany().getId());
    }

    private byte[] buildPdf(AiReport report, AiReportContentDto analysis) {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            document.add(new Paragraph("AI Inventory Analysis", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
            document.add(new Paragraph("Scope: " + report.getScopeLabel()));
            document.add(new Paragraph("Generated: " + report.getGeneratedOn()));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Overview", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            document.add(new Paragraph(analysis.getOverview()));
            document.add(new Paragraph(" "));
            addItemSection(document, "Depletion Risks", analysis.getDepletionRisks());
            addItemSection(document, "Oversupply Risks", analysis.getOversupplyRisks());
            addItemSection(document, "Suggested Purchases", analysis.getSuggestedPurchases());
            document.add(new Paragraph("Recommended Actions", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            for (String action : analysis.getActions()) {
                document.add(new Paragraph("- " + action));
            }
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create AI report PDF");
        }
    }

    private void addItemSection(Document document, String title, List<AiAnalysisItemDto> items) throws DocumentException {
        document.add(new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        if (items == null || items.isEmpty()) {
            document.add(new Paragraph("No items."));
            document.add(new Paragraph(" "));
            return;
        }

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        addCell(table, "Item");
        addCell(table, "Company");
        addCell(table, "Inventory");
        addCell(table, "Qty");
        addCell(table, "Reason");

        for (AiAnalysisItemDto item : items) {
            addCell(table, item.getItemName());
            addCell(table, item.getCompanyName());
            addCell(table, item.getInventoryName());
            addCell(table, String.valueOf(item.getQuantity()));
            addCell(table, item.getReason());
        }

        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addCell(PdfPTable table, String value) {
        table.addCell(new PdfPCell(new Phrase(value != null ? value : "")));
    }

    private record AiRequestContext(User currentUser, List<CompanyOptionDto> companies, Company selectedCompany) {
    }

}
