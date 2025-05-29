package com.cdata.mcp;

public class CsvWriter {
  private StringBuilder buffer = new StringBuilder();

  public Row row() {
    return new Row();
  }

  public String end() {
    return this.buffer.toString();
  }

  public class Row {
    private StringBuilder row = new StringBuilder();

    public Row column(String value) {
      if (row.length() > 0) {
        row.append(',');
      }
      if (value != null && value.length() > 0) {
        quote(value);
      }
      return this;
    }

    public void end() {
      buffer.append(this.row)
          .append("\n");
    }
    private void quote(String val) {
      row.append('"');
      for (int i=0; i < val.length(); i++) {
        char ch = val.charAt(i);
        if (ch == '"') {
          row.append(ch);
        }
        row.append(ch);
      }
      row.append('"');
    }
  }
}
