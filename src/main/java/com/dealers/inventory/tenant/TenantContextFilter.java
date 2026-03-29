package com.dealers.inventory.tenant;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(100)
@RequiredArgsConstructor
public class TenantContextFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant-Id";

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (!requiresTenantContext(path)) {
            try {
                filterChain.doFilter(request, response);
            } finally {
                TenantContext.clear();
            }
            return;
        }

        String raw = request.getHeader(TENANT_HEADER);
        if (raw == null || raw.isBlank()) {
            writeJsonError(
                    response, HttpServletResponse.SC_BAD_REQUEST, "Missing required header: " + TENANT_HEADER);
            return;
        }
        try {
            TenantContext.setTenantId(UUID.fromString(raw.trim()));
        } catch (IllegalArgumentException ex) {
            writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST, TENANT_HEADER + " must be a valid UUID");
            return;
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private void writeJsonError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Map.of("error", message)));
    }

    private boolean requiresTenantContext(String path) {
        if (path == null) {
            return false;
        }
        return path.startsWith("/dealers") || path.startsWith("/vehicles");
    }
}
