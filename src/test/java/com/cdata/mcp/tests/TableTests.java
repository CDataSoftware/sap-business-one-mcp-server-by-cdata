package com.cdata.mcp.tests;

import com.cdata.mcp.Table;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TableTests {
  @Test
  public void justName() {
    Table table = Table.parse("mytable");
    Assert.assertEquals("mytable", table.name());
  }
  @Test
  public void justSchemaName() {
    Table table = Table.parse("myschema.mytable");
    Assert.assertEquals("myschema", table.schema());
    Assert.assertEquals("mytable", table.name());
  }
  @Test
  public void fullName() {
    Table table = Table.parse("mycatalog.myschema.mytable");
    Assert.assertEquals("mycatalog", table.catalog());
    Assert.assertEquals("myschema", table.schema());
    Assert.assertEquals("mytable", table.name());
  }

  @Test
  public void quoted() {
    Table table = Table.parse("[mycatalog].[myschema].[mytable]");
    Assert.assertEquals("mycatalog", table.catalog());
    Assert.assertEquals("myschema", table.schema());
    Assert.assertEquals("mytable", table.name());
  }

  @Test
  public void spacesAtStartAreIgnored() {
    Table table = Table.parse("   [mycatalog].[myschema].[mytable]");
    Assert.assertEquals("mycatalog", table.catalog());
    Assert.assertEquals("myschema", table.schema());
    Assert.assertEquals("mytable", table.name());
  }

  @Test
  public void spacesAtEndAreIgnored() {
    Table table = Table.parse("[mycatalog].[myschema].mytable   ");
    Assert.assertEquals("mycatalog", table.catalog());
    Assert.assertEquals("myschema", table.schema());
    Assert.assertEquals("mytable", table.name());
  }

  @Test
  public void parseList() {
    String list = "[mycatalog].[myschema].mytable, cat2.schm2.t2, cat3.[scm3].t3  ";
    List<Table> tables = Table.parseList(list);

    Assert.assertEquals(3, tables.size());
    Table table = tables.get(0);
    Assert.assertEquals("mycatalog", table.catalog());
    Assert.assertEquals("myschema", table.schema());
    Assert.assertEquals("mytable", table.name());

    table = tables.get(1);
    Assert.assertEquals("cat2", table.catalog());
    Assert.assertEquals("schm2", table.schema());
    Assert.assertEquals("t2", table.name());

    table = tables.get(2);
    Assert.assertEquals("cat3", table.catalog());
    Assert.assertEquals("scm3", table.schema());
    Assert.assertEquals("t3", table.name());
  }

  @Test
  public void parseList2() {
    String list = "table1, table2";
    List<Table> tables = Table.parseList(list);

    Assert.assertEquals(2, tables.size());
    Table table = tables.get(0);
    Assert.assertEquals("table1", table.name());

    table = tables.get(1);
    Assert.assertEquals("table2", table.name());
  }

  @Test
  public void fromUri1() throws Exception {
    String uri = "salesforce://cat/schm/table";
    Table table = Table.fromUri(uri);
    Assert.assertNotNull(table);
    Assert.assertEquals("cat", table.catalog());
    Assert.assertEquals("schm", table.schema());
    Assert.assertEquals("table", table.name());
  }
}
