package com.cdata.mcp;

public class StringUtil {
  public static String emptyNull(String t) {
    if (t != null && t.length() == 0) {
      return null;
    }
    return t;
  }

  public static boolean isNullOrEmpty(String s) {
    return s == null || s.length() == 0;
  }

  public static String qualify(Config config, String catalog, String schema, String proc) {
    StringBuilder result = new StringBuilder();
    if (catalog != null && catalog.length() > 0) {
      result.append(config.quoteIdentifier(catalog))
          .append(".");
    }
    if (schema != null && schema.length() > 0) {
      result.append(config.quoteIdentifier(schema))
          .append(".");
    }
    result.append(config.quoteIdentifier(proc));
    return result.toString();
  }
}
