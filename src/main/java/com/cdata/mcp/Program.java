package com.cdata.mcp;

import java.sql.SQLException;
import java.util.List;

import com.cdata.mcp.resources.TableMetadataResource;
import com.cdata.mcp.tools.GetColumnsTool;
import com.cdata.mcp.tools.GetTablesTool;
import com.cdata.mcp.tools.RunQueryTool;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransport;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.ServerMcpTransport;

public class Program {
  
  private ServerMcpTransport transport;
  private Config config;
  private McpSyncServer mcpServer;
  private static final boolean STDIO = true;

  public void init(String configPath) throws Exception {
    this.config = new Config();
    this.config.load(configPath);
    if (!StringUtil.isNullOrEmpty(this.config.getLogFile())) {
      System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
      System.setProperty("org.slf4j.simpleLogger.logFile", this.config.getLogFile());
    }
    if (!this.config.validate(System.err)) {
      System.exit(-1);
    }

    this.transport = new StdioServerTransport(new ObjectMapper());
  }

  public void configureMcp() throws Exception {
    McpServer.SyncSpec spec =
        McpServer.sync(this.transport)
            .serverInfo(this.config.getServerName(), this.config.getServerVersion())
            .capabilities(
                McpSchema.ServerCapabilities.builder()
                    .tools(true)
                    .resources(false, true)
                    //.logging()
                    .build()
            );

    registerResources(this.config, spec);
    registerTools(this.config, spec);

    this.mcpServer = spec.build();
  }

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.err.println("Usage: <properties-file-path>");
      System.exit(-1);
    }
    String path = args[0];

    final Program p = new Program();
    p.init(args[0]);
    p.configureMcp();
    if (!STDIO) {
      //p.runHttpServer();
    } else {
      Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run() {
          synchronized (p) {
            p.notify();
          }
        }
      });
      synchronized (p) {
        p.wait();
        p.mcpServer.closeGracefully();
      }
    }
  }

  private static void registerResources(Config config, McpServer.SyncSpec mcp) throws SQLException {
    List<Table> tables = config.getTables();
    TableMetadataResource resource = new TableMetadataResource(config);

    for (Table r : tables) {
      resource.register(mcp, r);
    }
  }
  private static void registerTools(Config config, McpServer.SyncSpec mcp) throws Exception {
    ITool[] tools = new ITool[] {
        new GetTablesTool(config),
        new GetColumnsTool(config),
        new RunQueryTool(config)
    };
    for (ITool tool : tools) {
      if (tool != null) {
        tool.register(mcp);
      }
    }
  }
}