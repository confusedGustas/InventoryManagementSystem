package org.gustas.inventory.inventorymanagementsystem.common.mapper;

import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PagedResponseMapperTest {

    private final PagedResponseMapper pagedResponseMapper = new PagedResponseMapper();

    @Test
    void shouldMapPageMetadata() {
        PageImpl<String> page = new PageImpl<>(List.of("a", "b"), PageRequest.of(1, 10), 14);

        PagedResponseDto<String> result = pagedResponseMapper.toDto(page);

        assertThat(result.getContent()).containsExactly("a", "b");
        assertThat(result.getPage()).isEqualTo(2);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(12);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }
}
