package org.gustas.inventory.inventorymanagementsystem.domain.ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiPromptProviderTest {

    @Test
    void shouldLoadPromptResources() {
        Resource system = new ByteArrayResource("system".getBytes()) {
            @Override
            public String getFilename() {
                return "system.txt";
            }
        };

        AiPromptProvider provider = new AiPromptProvider(
                system,
                namedResource("analysis.txt", "analysis"),
                namedResource("item-system.txt", "item-system"),
                namedResource("item-user.txt", "item-user")
        );

        assertThat(provider.getSystemPrompt()).isEqualTo("system");
        assertThat(provider.getAnalysisPromptTemplate()).isEqualTo("analysis");
        assertThat(provider.getItemImageSystemPrompt()).isEqualTo("item-system");
        assertThat(provider.getItemImageUserPrompt()).isEqualTo("item-user");
    }

    @Test
    void shouldFailWhenPromptCannotBeRead() {
        Resource broken = new org.springframework.core.io.AbstractResource() {
            @Override
            public String getDescription() {
                return "broken";
            }

            @Override
            public String getFilename() {
                return "broken.txt";
            }

            @Override
            public java.io.InputStream getInputStream() throws java.io.IOException {
                throw new java.io.IOException("boom");
            }
        };

        assertThatThrownBy(() -> new AiPromptProvider(
                broken,
                namedResource("analysis.txt", "analysis"),
                namedResource("item-system.txt", "item-system"),
                namedResource("item-user.txt", "item-user")
        )).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to load AI prompt resource");
    }

    private Resource namedResource(String filename, String value) {
        return new ByteArrayResource(value.getBytes()) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
    }
}
