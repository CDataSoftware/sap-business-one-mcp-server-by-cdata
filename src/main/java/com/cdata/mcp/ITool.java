package com.cdata.mcp;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.spec.McpSchema;
import java.util.Map;

public interface ITool {
  public void register(McpServer.SyncSpec mcp) throws Exception;
  public McpSchema.CallToolResult run(Map<String, Object> args);
}
