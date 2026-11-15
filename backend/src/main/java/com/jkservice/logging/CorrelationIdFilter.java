package com.jkservice.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

  public static final String HEADER_NAME = "X-Correlation-ID";
  public static final String MDC_KEY = "correlationId";

  private static final Pattern VALID_CORRELATION_ID = Pattern.compile("^[a-zA-Z0-9._-]{1,64}$");

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String correlationId = request.getHeader(HEADER_NAME);

    if (correlationId == null || !VALID_CORRELATION_ID.matcher(correlationId).matches()) {
      correlationId = UUID.randomUUID().toString();
    }
    String previousCorrelationId = MDC.get(MDC_KEY);

    try {
      MDC.put(MDC_KEY, correlationId);
      response.setHeader(HEADER_NAME, correlationId);

      filterChain.doFilter(request, response);
    } finally {
      if (previousCorrelationId == null) {
        MDC.remove(MDC_KEY);
      } else {
        MDC.put(MDC_KEY, previousCorrelationId);
      }
    }
  }
}
