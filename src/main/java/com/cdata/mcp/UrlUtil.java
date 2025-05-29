package com.cdata.mcp;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class UrlUtil {
  public static String encode(String part) {
    return URLEncoder.encode(part, Charset.defaultCharset());
  }
  public static String decode(String part) {
    return URLDecoder.decode(part, Charset.defaultCharset());
  }
}
