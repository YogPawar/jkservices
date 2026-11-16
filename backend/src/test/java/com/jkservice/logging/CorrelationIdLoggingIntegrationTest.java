package com.jkservice.logging;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class CorrelationIdLoggingIntegrationTest {

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(new TestLoggingController())
            .addFilters(new CorrelationIdFilter())
            .build();
  }

  @Test
  void shouldPropagateCorrelationIdIntoApplicationLogs() throws Exception {

    String correlationId = "jk-integration-test-001";

    Logger logger = (Logger) LoggerFactory.getLogger(TestLoggingController.class);

    ListAppender<ILoggingEvent> appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);

    try {
      mockMvc
          .perform(
              get("/test/correlation-log").header(CorrelationIdFilter.HEADER_NAME, correlationId))
          .andExpect(status().isOk())
          .andExpect(content().string("ok"))
          .andExpect(header().string(CorrelationIdFilter.HEADER_NAME, correlationId));

      assertTrue(
          appender.list.stream()
              .anyMatch(event ->
                  correlationId.equals(event.getMDCPropertyMap().get(CorrelationIdFilter.MDC_KEY))),
          "Expected application log event to contain the request correlation ID");
    } finally {
      logger.detachAppender(appender);
      appender.stop();
    }
  }

  @RestController
  static class TestLoggingController {

    private static final org.slf4j.Logger log =
        LoggerFactory.getLogger(TestLoggingController.class);

    @GetMapping("/test/correlation-log")
    String logRequest() {
      log.info("Correlation ID integration test log");
      return "ok";
    }
  }
}
