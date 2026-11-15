
package com.jkservice.logging;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
@Import(CorrelationIdLoggingIntegrationTest.TestConfig.class)
class CorrelationIdLoggingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldPropagateCorrelationIdIntoApplicationLogs(CapturedOutput output)
            throws Exception {

        String correlationId = "jk-integration-test-001";

        mockMvc.perform(
                        get("/test/correlation-log")
                                .header(CorrelationIdFilter.HEADER_NAME, correlationId))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"))
                .andExpect(
                        header().string(
                                CorrelationIdFilter.HEADER_NAME, correlationId));

        assertTrue(
                output.getAll().contains("[corr=" + correlationId + "]"),
                "Expected captured logs to contain the request correlation ID");
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        TestLoggingController testLoggingController() {
            return new TestLoggingController();
        }
    }

    @RestController
    static class TestLoggingController {

        private static final Logger log =
                LoggerFactory.getLogger(TestLoggingController.class);

        @GetMapping("/test/correlation-log")
        String logRequest() {
            log.info("Correlation ID integration test log");
            return "ok";
        }
    }
}
