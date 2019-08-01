/**
 * 
 */
package io.alanda.base.util;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.search.sort.SortOrder;

/**
 * @author developer
 */
public class ElasticUtil {

  public static Map<String, Object> raw(Object value) {
    Map<String, Object> map = new HashMap<>();
    map.put("fieldType", "raw");
    if (value instanceof String) {
      map.put("value", value);
    } else {
      map.put("value", value.toString());
    }
    return map;
  }

  public static Map<String, Object> sort(int prio, SortOrder order) {
    Map<String, Object> map = new HashMap<>();
    map.put("prio", prio);
    map.put("dir", order.toString().toLowerCase());
    return map;
  }

  public static Map<String, Object> addSort(int prio, boolean asc) {
    Map<String, Object> map = new HashMap<>();
    map.put("prio", prio);
    map.put("dir", (asc ? "asc" : "desc"));
    return map;
  }

}
