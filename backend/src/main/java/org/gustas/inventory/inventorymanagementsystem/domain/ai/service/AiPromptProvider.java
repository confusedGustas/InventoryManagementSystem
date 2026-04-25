package org.gustas.inventory.inventorymanagementsystem.domain.ai.service;

import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Getter
@Component
public class AiPromptProvider {

    private final String systemPrompt;
    private final String analysisPromptTemplate;
    private final String itemImageSystemPrompt;
    private final String itemImageUserPrompt;

    public AiPromptProvider(
            @org.springframework.beans.factory.annotation.Value("classpath:prompts/ai/inventory-analysis-system-prompt.txt") Resource systemPromptResource,
            @org.springframework.beans.factory.annotation.Value("classpath:prompts/ai/inventory-analysis-user-prompt.txt") Resource analysisPromptTemplateResource,
            @org.springframework.beans.factory.annotation.Value("classpath:prompts/ai/item-image-system-prompt.txt") Resource itemImageSystemPromptResource,
            @org.springframework.beans.factory.annotation.Value("classpath:prompts/ai/item-image-user-prompt.txt") Resource itemImageUserPromptResource
    ) {
        this.systemPrompt = readResource(systemPromptResource);
        this.analysisPromptTemplate = readResource(analysisPromptTemplateResource);
        this.itemImageSystemPrompt = readResource(itemImageSystemPromptResource);
        this.itemImageUserPrompt = readResource(itemImageUserPromptResource);
    }

    private String readResource(Resource resource) {
        try {
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load AI prompt resource: " + resource.getFilename(), exception);
        }
    }

}
