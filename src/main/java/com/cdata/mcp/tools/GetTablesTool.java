package com.cdata.mcp.tools;

import com.cdata.mcp.*;

import static com.cdata.mcp.StringUtil.*;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetTablesTool implements ITool {
  private Config config;
  private Logger logger = LoggerFactory.getLogger(GetTablesTool.class);

  public GetTablesTool(Config config) {
    this.config = config;
  }

  public void register(McpServer.SyncSpec mcp) throws Exception {
    String schema = new JsonSchemaBuilder()
        .addString("catalog", "The catalog name")
        .addString("schema", "The schema name")
        .build();
    mcp.tool(
        new McpSchema.Tool(
            config.getPrefix() + "_get_tables",
            "Retrieves a list of objects, entities, collections, etc. (as tables) available in the data source. Use the `" + config.getPrefix() + "_get_columns` tool to list available columns on a table. " +
                "Both `catalog` and `schema` are optional parameters. " +
                Constants.FORMAT_DESC,
            schema
        ),
        this::run
    );
  }

  @Override
  public McpSchema.CallToolResult run(Map<String, Object> args) {
    String catalog = (String)args.get("catalog");
    String schema = (String)args.get("schema");

    this.logger.info("GetTablesTool({}, {})", catalog, schema);
    try {
      try (Connection cn = config.newConnection()) {
        List<McpSchema.Content> content = new ArrayList<>();
        String csv = tablesToCsv(cn, catalog, schema);

        List<McpSchema.Role> roles = new ArrayList<>();
        roles.add(McpSchema.Role.USER);
        content.add(
            new McpSchema.TextContent(roles, 1.0, csv)
        );
        if (!this.config.supportsMultipleCatalogs()) {
          content.add(
              new McpSchema.TextContent(roles, 1.0, "Default Catalog: " + this.config.defaultCatalog())
          );
        }
        if (!this.config.supportsMultipleSchemas()) {
          content.add(
              new McpSchema.TextContent(roles, 1.0, "Default Schema: " + this.config.defaultSchema())
          );
        }
        return new McpSchema.CallToolResult(content, false);
      }
    } catch ( Exception ex ) {
      throw new RuntimeException("ERROR: " + ex.getMessage());
    }
  }

  private String tablesToCsv(Connection cn, String catalog, String schema) throws SQLException {
    List<String[]> META_COLS = new ArrayList<String[]>();
    if (this.config.supportsMultipleCatalogs()) {
      META_COLS.add(new String[]{"TABLE_CAT", "Catalog"});
    }
    if (this.config.supportsMultipleSchemas()) {
      META_COLS.add(new String[]{"TABLE_SCHEM", "Schema"});
    }
    META_COLS.add(new String[] { "TABLE_NAME", "Table" });
    META_COLS.add(new String[] { "REMARKS", "Description" });

    DatabaseMetaData meta = cn.getMetaData();
    try (ResultSet rs = meta.getTables(emptyNull(catalog), emptyNull(schema), null, null)) {
      return CsvUtils.resultSetToCsv(rs, META_COLS.toArray(new String[0][]));
    }
  }
}
