package com.mcpshield.tool;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Tool;

/**
 * Mock internal MCP tool that returns safe dummy customer profile data.
 */
public final class GetCustomerProfileTool {

    public static final String TOOL_NAME = "getCustomerProfile";

    private GetCustomerProfileTool() {
    }

    public static McpServerFeatures.SyncToolSpecification specification() {
        Map<String, Object> inputSchema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "customerId", Map.of(
                                "type", "string",
                                "description", "Customer identifier")),
                "required", List.of("customerId"),
                "additionalProperties", false);

        Tool tool = Tool.builder(TOOL_NAME, inputSchema)
                .description("Returns a mock customer profile for the given customerId")
                .build();

        return McpServerFeatures.SyncToolSpecification.builder()
                .tool(tool)
                .callHandler((exchange, request) -> handle(request))
                .build();
    }

    static CallToolResult handle(McpSchema.CallToolRequest request) {
        Object customerIdArg = request.arguments() != null
                ? request.arguments().get("customerId")
                : null;

        if (customerIdArg == null || customerIdArg.toString().isBlank()) {
            return CallToolResult.builder()
                    .addTextContent("customerId is required")
                    .isError(true)
                    .build();
        }

        String customerId = customerIdArg.toString();

        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("customerId", customerId);
        profile.put("customerName", "Alice Johnson");
        profile.put("customerEmail", "alice.johnson@example.com");
        profile.put("accountStatus", "ACTIVE");

        return CallToolResult.builder()
                .addTextContent(
                        "customerId=" + customerId
                                + ", customerName=Alice Johnson"
                                + ", customerEmail=alice.johnson@example.com"
                                + ", accountStatus=ACTIVE")
                .structuredContent(profile)
                .isError(false)
                .build();
    }
}
