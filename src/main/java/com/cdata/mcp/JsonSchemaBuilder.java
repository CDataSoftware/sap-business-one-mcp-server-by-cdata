package com.cdata.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class JsonSchemaBuilder {
  public static final String STRING = "string";
  public static final String INTEGER = "integer";
  public static final String NUMBER = "number";
  public static final String BOOLEAN = "boolean";
  public static final String OBJECT = "object";
  public static final String SCHEMA_REF = "http://json-schema.org/draft-07/schema#";

  private List<Property> properties = new ArrayList<>();
  private List<String> required = new ArrayList<>();

  public JsonSchemaBuilder addString(String name, String description) {
    this.properties.add(new Property(name, STRING, description));
    return this;
  }

  public JsonSchemaBuilder addObject(String name, String description) {
    this.properties.add(new Property(name, OBJECT, description));
    return this;
  }

  public JsonSchemaBuilder add(String name, String type, String description) {
    this.properties.add(new Property(name, type, description));
    return this;
  }
  public JsonSchemaBuilder required(String... args) {
    for (String s : args ) {
      this.required.add(s);
    }
    return this;
  }

  public String build() throws Exception {
    Map<String, Object> props = new LinkedHashMap<>();
    for ( Property p : this.properties ) {
      Map<String, Object> prop = new LinkedHashMap<>();
      prop.put("type", p.type());
      if (p.hasDescription()) {
        prop.put("description", p.description());
      }
      props.put(p.name(), prop);
    }

    Map<String, Object> obj = new LinkedHashMap<>();
    obj.put("$schema", SCHEMA_REF);
    obj.put("type", OBJECT);
    obj.put("properties", props);
    obj.put("required", this.required);
    return new ObjectMapper().writeValueAsString(obj);
  }


  static class Property {
    private String _name;
    private String _type;
    private String _description;

    public Property(String name, String type, String desc) {
      this._name = name;
      this._type = type;
      this._description = desc;
    }

    public String name() { return this._name; }
    public String type() { return this._type; }
    public boolean hasDescription() {
      return this._description != null && this._description.length() > 0;
    }
    public String description() { return this._description; }
  }
}