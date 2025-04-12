package com.lfpsys.lfpsys_nfe_upload_services.security;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenAuthFilter extends OncePerRequestFilter {

  private static final String EXPECTED_TOKEN = "Bearer my-token";

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
    final var authHeader = request.getHeader("Authorization");

    if (EXPECTED_TOKEN.equalsIgnoreCase(authHeader)) {
      filterChain.doFilter(request, response);
    } else {
      response.setStatus(SC_UNAUTHORIZED);
      response.getWriter().write("Invalid Token!");
    }
  }
}

