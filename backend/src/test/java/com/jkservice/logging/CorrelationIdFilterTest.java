package com.jkservice.logging;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class CorrelationIdFilterTest {

  private CorrelationIdFilter filter;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @BeforeEach
  void setUp() {
    filter = new CorrelationIdFilter();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @AfterEach
  void cleanUp() {
    MDC.remove(CorrelationIdFilter.MDC_KEY);
  }

  @Test
  void shouldPropagateValidIncomingCorrelationId() throws ServletException, IOException {

    request.addHeader(CorrelationIdFilter.HEADER_NAME, "request-123");

    FilterChain chain =
        (req, res) -> assertEquals("request-123", MDC.get(CorrelationIdFilter.MDC_KEY));

    filter.doFilter(request, response, chain);

    assertEquals("request-123", response.getHeader(CorrelationIdFilter.HEADER_NAME));
    assertNull(MDC.get(CorrelationIdFilter.MDC_KEY));
  }

  @Test
  void shouldGenerateCorrelationIdWhenHeaderIsMissing() throws ServletException, IOException {

    FilterChain chain = (req, res) -> assertNotNull(MDC.get(CorrelationIdFilter.MDC_KEY));

    filter.doFilter(request, response, chain);

    String correlationId = response.getHeader(CorrelationIdFilter.HEADER_NAME);

    assertNotNull(correlationId);
    assertDoesNotThrow(() -> java.util.UUID.fromString(correlationId));
    assertNull(MDC.get(CorrelationIdFilter.MDC_KEY));
  }

  @Test
  void shouldGenerateNewIdWhenIncomingHeaderIsInvalid() throws ServletException, IOException {

    request.addHeader(CorrelationIdFilter.HEADER_NAME, "invalid correlation id!");

    filter.doFilter(
        request,
        response,
        (req, res) ->
            assertDoesNotThrow(
                () -> java.util.UUID.fromString(MDC.get(CorrelationIdFilter.MDC_KEY))));

    String correlationId = response.getHeader(CorrelationIdFilter.HEADER_NAME);

    assertNotNull(correlationId);
    assertDoesNotThrow(() -> java.util.UUID.fromString(correlationId));
  }

  @Test
  void shouldClearMdcWhenRequestProcessingThrowsException() {

    assertThrows(
        IOException.class,
        () ->
            filter.doFilter(
                request,
                response,
                (req, res) -> {
                  assertNotNull(MDC.get(CorrelationIdFilter.MDC_KEY));
                  throw new IOException("Simulated downstream failure");
                }));

    assertNull(MDC.get(CorrelationIdFilter.MDC_KEY));
  }

  @Test
  void shouldRestorePreviousMdcValueAfterRequestProcessing() throws ServletException, IOException {

    MDC.put(CorrelationIdFilter.MDC_KEY, "upstream-456");
    request.addHeader(CorrelationIdFilter.HEADER_NAME, "request-123");

    FilterChain chain =
        (req, res) -> assertEquals("request-123", MDC.get(CorrelationIdFilter.MDC_KEY));

    filter.doFilter(request, response, chain);

    assertEquals("upstream-456", MDC.get(CorrelationIdFilter.MDC_KEY));
  }
}
