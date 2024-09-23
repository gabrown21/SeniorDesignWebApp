package edu.bu;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Probe")
public class JSONProbe {

  static final String PROBE_STRING =
      "{\"data\":" + "[" + "{\"p\":215,\"s\":\"AAPL\",\"t\":1704067200000,\"v\":1}," + "]" + "}";
  static final String PROBE_STRING_NO_PRICE =
      "{\"data\":" + "[" + "{\"s\":\"AAPL\",\"t\":1704067200000,\"v\":1}," + "]" + "}";
  static final String PROBE_STRING_WRONG =
      "{\"data\":" + "[{\"s\":\"AAPL\",\"t\":1704067200000,\"v\":1}," + "]";

  @Test
  public void jsonProbe() throws ParseException {
    JSONParser parser = new JSONParser();
    JSONObject object = (JSONObject) parser.parse(PROBE_STRING);
    JSONArray arrayData = (JSONArray) object.get("data");
    JSONObject firstPoint = (JSONObject) arrayData.get(0);

    long price = (long) firstPoint.get("p");
    // Used to find type
    // System.out.println("Type of 'p': " + priceObject.getClass().getName());
    System.out.println(price);
  }

  @Test
  public void jsonProbe_noPrice() throws ParseException {
    JSONParser parser = new JSONParser();
    JSONObject object = (JSONObject) parser.parse(PROBE_STRING_NO_PRICE);
    JSONArray arrayData = (JSONArray) object.get("data");
    JSONObject firstPoint = (JSONObject) arrayData.get(0);

    Long price = (Long) firstPoint.get("p");
    System.out.println(price);
  }

  @Test
  public void jsonProbe_forceThrow() throws ParseException {
    JSONParser parser = new JSONParser();
    JSONObject object = (JSONObject) parser.parse(PROBE_STRING_WRONG);
  }
}
