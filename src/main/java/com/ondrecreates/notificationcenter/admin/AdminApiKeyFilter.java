package com.ondrecreates.notificationcenter.admin;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Admin API je záměrně oddělená od per-klient X-API-Key autentizace (ApiKeyAuthFilter) –
 * admin musí vidět notifikace napříč všemi klienty, což by narušilo ownership model
 * navržený pro /api/v1/notifications. Pro MVP stačí jeden sdílený admin token
 * (env proměnná), ne plnohodnotné uživatelské účty s rolemi – to by bylo zbytečné
 * pro portfolio demo s jedním adminem.
 */
public class AdminApiKeyFilter extends OncePerRequestFilter {

    private static final String ADMIN_KEY_HEADER = "X-Admin-Key";

    private final String expectedAdminKey;

    public AdminApiKeyFilter(String expectedAdminKey) {
        this.expectedAdminKey = expectedAdminKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String adminKey = request.getHeader(ADMIN_KEY_HEADER);
        if (adminKey == null || !constantTimeEquals(adminKey, expectedAdminKey)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\":\"Chybí nebo neplatný X-Admin-Key.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // String.equals() vrací výsledek rychleji u neshody na prvním znaku –
    // teoreticky umožňuje timing útok na tajný klíč. MessageDigest.isEqual
    // porovnává vždy ve stejném čase bez ohledu na to, kde se řetězce liší.
    private boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
