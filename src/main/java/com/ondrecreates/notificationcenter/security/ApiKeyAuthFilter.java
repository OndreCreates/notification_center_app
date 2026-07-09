package com.ondrecreates.notificationcenter.security;

import com.ondrecreates.notificationcenter.client.Client;
import com.ondrecreates.notificationcenter.client.ClientRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    public static final String CLIENT_ATTRIBUTE = "client";
    private static final String API_KEY_HEADER = "X-API-Key";

    private final ClientRepository clientRepository;

    public ApiKeyAuthFilter(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader(API_KEY_HEADER);
        if (apiKey == null || apiKey.isBlank()) {
            respondUnauthorized(response, "Chybí API klíč v hlavičce X-API-Key.");
            return;
        }

        Optional<Client> client = clientRepository.findByApiKeyHash(ApiKeyHasher.hash(apiKey));
        if (client.isEmpty() || !client.get().isActive()) {
            respondUnauthorized(response, "Neplatný API klíč.");
            return;
        }

        request.setAttribute(CLIENT_ATTRIBUTE, client.get());
        filterChain.doFilter(request, response);
    }

    private void respondUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"%s\"}".formatted(message));
    }
}
