package com.cdata.mcp;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class CsvUtils {
  public static final String MIME = "text/csv";
  public static String resultSetToCsv(ResultSet rs) throws SQLException {
    return resultSetToCsv(rs, null);
  }

  public static String resultSetToCsv(ResultSet rs, String[][] columns) throws SQLException {
    CsvWriter csv = new CsvWriter();
    ResultSetMetaData meta = rs.getMetaData();
    writeMeta(csv, meta, columns);
    while (rs.next()) {
      writeRow(csv, meta, columns, rs);
    }
    rs.close();
    return csv.end();
  }

  private static void writeMeta(CsvWriter csv, ResultSetMetaData meta, String[][] columns) throws SQLException {
    CsvWriter.Row row = csv.row();
    if (columns == null) {
      for (int i = 1; i <= meta.getColumnCount(); i++) {
        row.column(meta.getColumnLabel(i));
      }
    } else {
      for ( int i=0; i < columns.length; i++ ) {
        row.column(columns[i][1]);
      }
    }
    row.end();
  }
  private static void writeRow(CsvWriter csv, ResultSetMetaData meta, String[][] columns, ResultSet rs) throws SQLException {
    CsvWriter.Row row = csv.row();
    if ( columns == null ) {
      for (int i = 1; i <= meta.getColumnCount(); i++) {
        row.column(rs.getString(i));
      }
    } else {
      for (int i = 0; i < columns.length; i++) {
        row.column(rs.getString(columns[i][0]));
      }
    }
    row.end();
  }
}
