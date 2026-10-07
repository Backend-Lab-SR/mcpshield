package com.mcpshield.mcp;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mcpshield.tool.GetCustomerProfileTool;

import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;

/**
 * Minimal MCP server wiring using the official MCP Java SDK servlet transport.
 */
@Configuration
public class McpServerConfig {

    public static final String MCP_ENDPOINT = "/mcp";

    @Bean
    HttpServletStreamableServerTransportProvider mcpTransportProvider() {
        return HttpServletStreamableServerTransportProvider.builder()
                .jsonMapper(McpJsonDefaults.getMapper())
                .mcpEndpoint(MCP_ENDPOINT)
                .build();
    }

    @Bean
    ServletRegistrationBean<HttpServletStreamableServerTransportProvider> mcpServlet(
            HttpServletStreamableServerTransportProvider mcpTransportProvider) {
        ServletRegistrationBean<HttpServletStreamableServerTransportProvider> registration =
                new ServletRegistrationBean<>(mcpTransportProvider, MCP_ENDPOINT);
        registration.setName("mcpStreamableServlet");
        registration.setLoadOnStartup(1);
        registration.setAsyncSupported(true);
        return registration;
    }

    @Bean(destroyMethod = "close")
    McpSyncServer mcpSyncServer(HttpServletStreamableServerTransportProvider mcpTransportProvider) {
        return McpServer.sync(mcpTransportProvider)
                .serverInfo("mcpshield-internal-tools", "0.0.1")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .build())
                .tools(GetCustomerProfileTool.specification())
                .build();
    }
}
