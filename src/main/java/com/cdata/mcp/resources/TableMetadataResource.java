package com.cdata.mcp.resources;

import com.cdata.mcp.*;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TableMetadataResource implements IResource {
  private Config config;
  private static final String[][] META_COLS = new String[][] {
      new String[] { "TABLE_CAT", "Catalog" },
      new String[] { "TABLE_SCHEM", "Schema" },
      new String[] { "TABLE_NAME", "Table" },
      new String[] { "COLUMN_NAME", "Column" },
      new String[] { "TYPE_NAME", "DataType" }
  };

  public TableMetadataResource(Config config) {
    this.config = config;
  }

  @Override
  public void register(McpServer.SyncSpec mcp, Table table) {
    String uri = config.getMcpScheme() + table.urlPath();
    McpServerFeatures.SyncResourceRegistration resource = new McpServerFeatures.SyncResourceRegistration(
        new McpSchema.Resource(uri, table.fullName(), "List of columns for table " + table.fullName(), CsvUtils.MIME, null),
        this::run
    );
    mcp.resources(resource);
  }

  @Override
  public McpSchema.ReadResourceResult run(McpSchema.ReadResourceRequest args) {
    try {
      Table table = Table.fromUri(args.uri());

      try (Connection cn = config.newConnection()) {
        ResultSet rs = cn.getMetaData().getColumns(table.catalog(), table.schema(), table.name(), null);
        List<McpSchema.ResourceContents> contents = new ArrayList<>();
        contents.add(
            new McpSchema.TextResourceContents(args.uri(), CsvUtils.MIME, CsvUtils.resultSetToCsv(rs, META_COLS))
        );
        return new McpSchema.ReadResourceResult(contents);
      }
    } catch (Exception ex) {
      throw new RuntimeException("Error: " + ex.getMessage());
    }
  }

}
