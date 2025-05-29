package com.cdata.mcp;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Table {
  private String _catalog;
  private String _schema;
  private String _name;

  private Table() {
  }
  public Table(String cat, String sch, String n) {
    this._catalog = cat;
    this._schema = sch;
    this._name = n;
  }

  public boolean hasCatalog() {
    return this._catalog != null && this._catalog.length() > 0;
  }
  public String catalog() {
    return this._catalog;
  }
  public boolean hasSchema() {
    return this._schema != null && this._schema.length() > 0;
  }
  public String schema() {
    return this._schema;
  }
  public String name() {
    return this._name;
  }

  public static Table parse(String text) {
    Tokenizer t = new Tokenizer(text);
    return parseInt(t);
  }

  public static List<Table> parseList(String text) {
    List<Table> list = new ArrayList<>();
    Tokenizer t = new Tokenizer(text);
    while (!t.eof()) {
      if (list.size() > 0) {
        t.skipListDelimiter();
      }
      Table table = parseInt(t);
      if (table != null) {
        list.add(table);
      }
    }
    return list;
  }

  public static Table fromUri(String uri) throws Exception {
    URI u = new URI(uri);
    String path = u.getAuthority() + u.getPath();
    String[] parts = path.split("/");
    if (parts.length == 3) {
      return new Table(UrlUtil.decode(parts[0]), UrlUtil.decode(parts[1]), UrlUtil.decode(parts[2]));
    } else if (parts.length == 2) {
      return new Table("", UrlUtil.decode(parts[0]), UrlUtil.decode(parts[1]));
    } else {
      return new Table("", "", UrlUtil.decode(parts[0]));
    }
  }

  public String urlPath() {
    StringBuilder path = new StringBuilder();
    if (this._catalog != null && this._catalog.length() > 0) {
      path.append(UrlUtil.encode(this._catalog));
    }
    path.append("/");
    if (this._schema != null && this._schema.length() > 0) {
      path.append(UrlUtil.encode(this._schema));
    }
    path.append("/");
    path.append(UrlUtil.encode(this._name));
    return path.toString();
  }

  public String fullName() {
    StringBuilder full = new StringBuilder();
    if (this._catalog != null && this._catalog.length() > 0) {
      full.append('[').append(this._catalog).append(']');
    }
    full.append(".");
    if (this._schema != null && this._schema.length() > 0) {
      full.append('[').append(this._schema).append(']');
    }
    full.append(".");
    full.append('[').append(this._name).append(']');
    return full.toString();
  }

  private static Table parseInt(Tokenizer t) {
    List<String> names = new ArrayList<String>();
    do {
      String token = t.next();
      if (".".equals(token)) {
        names.add("");
        token = t.next();
      } else {
        names.add(token);
      }
      token = t.lookahead();
      if (".".equals(token)) {
        t.next(); // discard
      } else {
        break;
      }
    } while (true);

    if (names.size() == 3) {
      return new Table(names.get(0), names.get(1), names.get(2));
    } else if (names.size() == 2) {
      return new Table("", names.get(0), names.get(1));
    } else if (names.size() == 1) {
      return new Table("", "", names.get(0));
    }
    throw new RuntimeException("Invalid table name");
  }


  private static class Tokenizer {
    private String text;
    private int offset;
    private int mark;

    public Tokenizer(String t) {
      this.text = t;
      this.offset = -1;
    }

    public boolean eof() {
      return available() <= 0;
    }

    public int available() {
      if (this.offset < 0) {
        return this.text.length();
      }
      return this.text.length() - this.offset - 1;
    }

    public void mark() {
      this.mark = this.offset;
    }

    public void rollback() {
      this.offset = this.mark;
    }

    public char peek() {
      if (available() > 0) {
        return this.text.charAt(this.offset+1);
      }
      return '\0';
    }
    public char nextChar() {
      if (available() > 0) {
        this.offset++;
        return this.text.charAt(this.offset);
      }
      return '\0';
    }

    public String lookahead() {
      this.mark();
      String n = next();
      this.rollback();
      return n;
    }

    public String next() {
      StringBuilder token = new StringBuilder();
      boolean quoted = false;
      skipWhitespace();

      while (!this.eof()) {
        char p = peek();
        if (token.length() == 0) {
          if (p == '[') {
            quoted = true;
            nextChar(); // consume
            continue;
          }
          token.append(p);
          nextChar(); // consume
        }
        if (!quoted && !isValidChar(p)) {
          skipWhitespace();
          break;
        } else if (quoted && p == ']'){
          quoted = false;
          nextChar(); // consume ]
          break;
        } else {
          token.append(nextChar());
        }
      }
      return token.toString();
    }

    private boolean isValidChar(char ch) {
      return Character.isLetter(ch) || Character.isDigit(ch);
    }
    private void skipWhitespace() {
      while (!eof()) {
        char p = peek();
        if (p != ' ') {
          break;
        }
        nextChar();
      }
    }
    private void skipToDot() {
      while (!eof()) {
        char p = peek();
        if (p == '.') {
          break;
        }
        nextChar();
      }
    }
    private void skipListDelimiter() {
      while (!eof()) {
        char p = nextChar();
        if (p == ',') {
          break;
        }
      }
    }
  }
}
