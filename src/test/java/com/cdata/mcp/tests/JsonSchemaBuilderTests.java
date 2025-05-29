package com.cdata.mcp.tests;

import com.cdata.mcp.JsonSchemaBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JsonSchemaBuilderTests {

  @Test
  public void simpleStringProperty() throws Exception {
    JsonSchemaBuilder builder = new JsonSchemaBuilder();
    builder.addString("myprop", "my desc");
    String result = builder.build();

    Map<String, Object> obj = deserialize(result);
    Assert.assertEquals(JsonSchemaBuilder.SCHEMA_REF, obj.get("$schema"));
    Assert.assertEquals(JsonSchemaBuilder.OBJECT, obj.get("type"));
    Map<String, Object> props = (Map<String, Object>)obj.get("properties");
    Map<String, Object> myprop = (Map<String, Object>)props.get("myprop");
    Assert.assertEquals(JsonSchemaBuilder.STRING, myprop.get("type"));
    Assert.assertEquals("my desc", myprop.get("description"));
  }

  @Test
  public void simpleObjectProperty() throws Exception {
    JsonSchemaBuilder builder = new JsonSchemaBuilder();
    builder.addObject("myprop", "my desc");
    String result = builder.build();

    Map<String, Object> obj = deserialize(result);
    Assert.assertEquals(JsonSchemaBuilder.SCHEMA_REF, obj.get("$schema"));
    Assert.assertEquals(JsonSchemaBuilder.OBJECT, obj.get("type"));
    Map<String, Object> props = (Map<String, Object>)obj.get("properties");
    Map<String, Object> myprop = (Map<String, Object>)props.get("myprop");
    Assert.assertEquals(JsonSchemaBuilder.OBJECT, myprop.get("type"));
    Assert.assertEquals("my desc", myprop.get("description"));
  }

  private Map<String, Object> deserialize(String result) throws Exception {
    TypeFactory factory = TypeFactory.defaultInstance();
    MapType type = factory.constructMapType(HashMap.class, String.class, Object.class);
    return new ObjectMapper().readValue(result, type);
  }
}
