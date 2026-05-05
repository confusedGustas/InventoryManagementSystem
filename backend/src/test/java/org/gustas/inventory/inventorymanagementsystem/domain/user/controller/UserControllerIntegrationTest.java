package org.gustas.inventory.inventorymanagementsystem.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseWithCompaniesDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.UserDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.service.UserService;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class UserControllerIntegrationTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldChangeEmail() throws Exception {
        var dto = TestDataFactory.changeEmailDto();
        dto.setEmail("new@example.com");
        when(userService.changeEmail(any(), any())).thenReturn(UserDto.builder().email("new@example.com").build());

        mockMvc.perform(patch("/users/email")
                        .principal(new TestingAuthenticationToken("employee", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userService).changeEmail(any(), any());
    }

    @Test
    void shouldGetUsers() throws Exception {
        when(userService.getUsers(2, "manager")).thenReturn(PagedResponseWithCompaniesDto.<UserDto, CompanyOptionDto>builder()
                .content(java.util.List.of(UserDto.builder().username("employee").build()))
                .companies(java.util.List.of())
                .page(2)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .build());

        mockMvc.perform(get("/users")
                        .principal(new TestingAuthenticationToken("manager", null))
                        .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("employee"));

        verify(userService).getUsers(2, "manager");
    }

    @Test
    void shouldGetProfile() throws Exception {
        when(userService.getProfile("employee")).thenReturn(UserDto.builder().username("employee").build());

        mockMvc.perform(get("/users/profile")
                        .principal(new TestingAuthenticationToken("employee", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("employee"));

        verify(userService).getProfile("employee");
    }

    @Test
    void shouldCreateUser() throws Exception {
        var dto = TestDataFactory.createUserDto(TestDataFactory.company().getId());
        dto.setEmail("jane@example.com");
        when(userService.createUser(any(), eq("manager"))).thenReturn(UserDto.builder().username("jane").build());

        mockMvc.perform(post("/users")
                        .principal(new TestingAuthenticationToken("manager", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userService).createUser(any(), eq("manager"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        var userId = TestDataFactory.companyUser(TestDataFactory.company()).getId();
        var dto = TestDataFactory.updateUserDto(TestDataFactory.company().getId());
        dto.setEmail("janet@example.com");
        when(userService.updateUser(eq(userId), any(), eq("manager"))).thenReturn(UserDto.builder().username("janet").build());

        mockMvc.perform(put("/users/{userId}", userId)
                        .principal(new TestingAuthenticationToken("manager", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userService).updateUser(eq(userId), any(), eq("manager"));
    }

    @Test
    void shouldChangePassword() throws Exception {
        when(userService.changePassword(eq("employee"), any())).thenReturn(UserDto.builder().username("employee").build());

        mockMvc.perform(patch("/users/change-password")
                        .principal(new TestingAuthenticationToken("employee", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestDataFactory.changePasswordDto())))
                .andExpect(status().isOk());

        verify(userService).changePassword(eq("employee"), any());
    }

    @Test
    void shouldDisableUser() throws Exception {
        var userId = TestDataFactory.companyUser(TestDataFactory.company()).getId();
        when(userService.disableUser(userId, "manager")).thenReturn(UserDto.builder().enabled(false).build());

        mockMvc.perform(patch("/users/{userId}/disable", userId)
                        .principal(new TestingAuthenticationToken("manager", null)))
                .andExpect(status().isOk());

        verify(userService).disableUser(userId, "manager");
    }

    @Test
    void shouldEnableUser() throws Exception {
        var userId = TestDataFactory.companyUser(TestDataFactory.company()).getId();
        when(userService.enableUser(userId, "manager")).thenReturn(UserDto.builder().enabled(true).build());

        mockMvc.perform(patch("/users/{userId}/enable", userId)
                        .principal(new TestingAuthenticationToken("manager", null)))
                .andExpect(status().isOk());

        verify(userService).enableUser(userId, "manager");
    }
}
