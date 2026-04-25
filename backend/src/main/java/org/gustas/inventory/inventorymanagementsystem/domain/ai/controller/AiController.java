package org.gustas.inventory.inventorymanagementsystem.domain.ai.controller;

import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiAnalysisDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiItemSuggestionDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiReportDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiScopeDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.service.AiService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN', 'COMPANY_USER', 'COMPANY_FINANCE')")
public class AiController {

    private final AiService aiService;

    @GetMapping("/scope")
    public AiScopeDto getScope(Authentication authentication, @RequestParam(name = "companyId", required = false) UUID companyId) {
        return aiService.getScope(companyId, authentication.getName());
    }

    @GetMapping("/analysis")
    public AiAnalysisDto getAnalysis(Authentication authentication, @RequestParam(name = "companyId", required = false) UUID companyId) {
        return aiService.analyze(companyId, authentication.getName());
    }

    @PostMapping(value = "/item-suggestion", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AiItemSuggestionDto suggestItem(
            Authentication authentication,
            @RequestParam(name = "companyId", required = false) UUID companyId,
            @RequestPart("image") MultipartFile image
    ) {
        return aiService.suggestItem(companyId, image, authentication.getName());
    }

    @GetMapping("/reports")
    public List<AiReportDto> getReports(Authentication authentication, @RequestParam(name = "companyId", required = false) UUID companyId) {
        return aiService.getReports(companyId, authentication.getName());
    }

    @GetMapping("/reports/{reportId}/pdf")
    public ResponseEntity<byte[]> downloadReportPdf(Authentication authentication, @PathVariable UUID reportId) {
        byte[] pdf = aiService.downloadReportPdf(reportId, authentication.getName());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ai-report-" + reportId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
