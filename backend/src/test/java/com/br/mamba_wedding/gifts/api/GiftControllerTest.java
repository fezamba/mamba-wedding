package com.br.mamba_wedding.gifts.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.br.mamba_wedding.config.security.SecurityFilter;
import com.br.mamba_wedding.gifts.api.dto.GiftCreated;
import com.br.mamba_wedding.gifts.application.GiftService;
import com.br.mamba_wedding.gifts.domain.Gift;

@WebMvcTest(
    controllers = GiftController.class,
    excludeFilters = {
        @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityFilter.class)
    }
)

// FIXME: teste está retornando 401
@Import(GiftControllerTest.TestSecurityConfig.class)
class GiftControllerTest {

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .httpBasic(Customizer.withDefaults())
                    .build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GiftService giftService;

    private String validBody() {
        return """
            {
              "name": "Geladeira",
              "description": "Geladeira duas portas preta",
              "value": 2500.00,
              "totalQuotas": 10,
              "imageUrl": "url-geladeira-duas-portas",
              "purchaseLink": "url-compra-geladeira"
            }
            """;
    }

    private Gift sampleGift() {
        return Gift.builder()
                .name("Geladeira")
                .description("Geladeira duas portas preta")
                .value(new BigDecimal("2500.00"))
                .imageUrl("url-geladeira-duas-portas")
                .purchaseLink("url-compra-geladeira")
                .totalQuotas(10)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void register_ShouldAllowAdmin() throws Exception {
        when(giftService.register(any())).thenReturn(new GiftCreated(sampleGift()));

        mockMvc.perform(post("/api/gifts/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validBody()))
                .andExpect(status().isCreated());

        verify(giftService).register(any());
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void register_ShouldReturnForbidden_WhenUserIsGuest() throws Exception {
        mockMvc.perform(post("/api/gifts/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validBody()))
                .andExpect(status().isForbidden());

        verify(giftService, never()).register(any());
    }
}