package org.gustas.inventory.inventorymanagementsystem.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.dto.TokenDto;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.service.AuthService;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerIntegrationTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService))
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldLogin() throws Exception {
        when(authService.login(any())).thenReturn(TokenDto.builder().token("jwt").build());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestDataFactory.loginDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt"));

        verify(authService).login(any());
    }

    @Test
    void shouldValidateRegisterRequest() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"\",\"lastName\":\"Doe\",\"username\":\"jane\",\"email\":\"bad\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
