package com.cdata.mcp;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

public interface IResource {
  public void register(McpServer.SyncSpec mcp, Table table);
  public McpSchema.ReadResourceResult run(McpSchema.ReadResourceRequest args);
}
