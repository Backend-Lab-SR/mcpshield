package com.mcpshield.tool;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;

class GetCustomerProfileToolTest {

    @Test
    void returnsDummyCustomerProfile() {
        CallToolResult result = GetCustomerProfileTool.handle(
                McpSchema.CallToolRequest.builder(GetCustomerProfileTool.TOOL_NAME)
                        .arguments(Map.of("customerId", "CUST-001"))
                        .build());

        assertThat(result.isError()).isFalse();
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst()).isInstanceOf(TextContent.class);

        TextContent text = (TextContent) result.content().getFirst();
        assertThat(text.text())
                .contains("CUST-001", "Alice Johnson", "alice.johnson@example.com", "ACTIVE");

        @SuppressWarnings("unchecked")
        Map<String, Object> profile = (Map<String, Object>) result.structuredContent();
        assertThat(profile)
                .containsEntry("customerId", "CUST-001")
                .containsEntry("customerName", "Alice Johnson")
                .containsEntry("customerEmail", "alice.johnson@example.com")
                .containsEntry("accountStatus", "ACTIVE");
    }

    @Test
    void rejectsMissingCustomerId() {
        CallToolResult result = GetCustomerProfileTool.handle(
                McpSchema.CallToolRequest.builder(GetCustomerProfileTool.TOOL_NAME)
                        .arguments(Map.of())
                        .build());

        assertThat(result.isError()).isTrue();
        assertThat(result.content().getFirst()).isInstanceOf(TextContent.class);
        assertThat(((TextContent) result.content().getFirst()).text())
                .contains("customerId is required");
    }
}
