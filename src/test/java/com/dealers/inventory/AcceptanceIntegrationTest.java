package com.dealers.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dealers.inventory.common.domain.VehicleStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AcceptanceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void missingXTenantIdReturns400() throws Exception {
        mockMvc.perform(get("/dealers").with(httpBasic("tenant_user", "password")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crossTenantAccessReturns403() throws Exception {
        UUID t1 = UUID.randomUUID();
        UUID t2 = UUID.randomUUID();

        String body = objectMapper.writeValueAsString(Map.of(
                "name", "Acme",
                "email", "acme@example.com",
                "subscriptionType", "BASIC"));

        String json = mockMvc.perform(post("/dealers")
                        .header("X-Tenant-Id", t1.toString())
                        .with(httpBasic("tenant_user", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID dealerId = UUID.fromString(objectMapper.readTree(json).get("id").asText());

        mockMvc.perform(get("/dealers/" + dealerId)
                        .header("X-Tenant-Id", t2.toString())
                        .with(httpBasic("tenant_user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void subscriptionPremiumFilterIsTenantScoped() throws Exception {
        UUID tenant = UUID.randomUUID();

        String basicDealer = objectMapper.writeValueAsString(Map.of(
                "name", "Basic Motors",
                "email", "basic@example.com",
                "subscriptionType", "BASIC"));
        String premiumDealer = objectMapper.writeValueAsString(Map.of(
                "name", "Premium Motors",
                "email", "premium@example.com",
                "subscriptionType", "PREMIUM"));

        String basicJson = mockMvc.perform(post("/dealers")
                        .header("X-Tenant-Id", tenant.toString())
                        .with(httpBasic("tenant_user", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(basicDealer))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String premiumJson = mockMvc.perform(post("/dealers")
                        .header("X-Tenant-Id", tenant.toString())
                        .with(httpBasic("tenant_user", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(premiumDealer))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID basicId = UUID.fromString(objectMapper.readTree(basicJson).get("id").asText());
        UUID premiumId = UUID.fromString(objectMapper.readTree(premiumJson).get("id").asText());

        mockMvc.perform(post("/vehicles")
                        .header("X-Tenant-Id", tenant.toString())
                        .with(httpBasic("tenant_user", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "dealerId",
                                basicId.toString(),
                                "model",
                                "Sedan-B",
                                "price",
                                new BigDecimal("20000.00"),
                                "status",
                                "AVAILABLE"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/vehicles")
                        .header("X-Tenant-Id", tenant.toString())
                        .with(httpBasic("tenant_user", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "dealerId",
                                premiumId.toString(),
                                "model",
                                "Sedan-P",
                                "price",
                                new BigDecimal("35000.00"),
                                "status",
                                VehicleStatus.AVAILABLE))))
                .andExpect(status().isCreated());

        String list = mockMvc.perform(get("/vehicles")
                        .param("subscription", "PREMIUM")
                        .header("X-Tenant-Id", tenant.toString())
                        .with(httpBasic("tenant_user", "password")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(list).contains("Sedan-P");
        assertThat(list).doesNotContain("Sedan-B");
    }

    @Test
    void adminCountsRequiresGlobalAdmin() throws Exception {
        mockMvc.perform(get("/admin/dealers/countBySubscription").with(httpBasic("tenant_user", "password")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/admin/dealers/countBySubscription")
                        .with(httpBasic("global_admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.BASIC").exists())
                .andExpect(jsonPath("$.PREMIUM").exists());
    }
}
