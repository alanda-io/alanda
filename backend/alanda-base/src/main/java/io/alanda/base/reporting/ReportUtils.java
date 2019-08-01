/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.reporting;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Row;

import io.alanda.base.dto.reporting.ElasticEntryDto;

/**
 * @author developer
 */
public class ReportUtils {

  public static final int MAX_ROW_HEIGHT = 5;

  /**
   * Given a Json object as map and a property path as String array
   * (ie {"my obj", "mykey"} instead of obj.mykey) returns the value of the property as Object
   * if the last element of the property path contains a boolean 'OR'
   * two alternates property in the same hierarchy level will be checked for a possible value
   * @param map
   * @param fieldPath
   * @return
   */
  public static Object getFieldValue(Object map, String[] fieldPath) {
    if (map != null) {
      for (String field : fieldPath) {
        boolean orHit = false;
        for (String orField: field.split(Pattern.quote("||"))) {
          if (((Map<String, Object>)map).containsKey(orField)) {
            map = ((Map<String, Object>)map).get(orField);
            orHit = true;
            break;
          }
        }
        if (!orHit) map = null;
        if (!(map instanceof Map)) {
          break;
        }
      }
    }
    return map;
  }

  public static String getObjectAsString(Object o) {
    if (o instanceof List) {
      String tmp = "";
      for (Object element : (List<Object>) o) {
        tmp += element.toString() + " ";
      }
      return ((String) tmp.trim());
    } else if (o instanceof String) {
      return (String) o;
    } else if (o instanceof Integer) {
      Integer.toString((Integer) o);
    } else if (o instanceof Boolean) {
      Boolean.toString((Boolean) o);
    } else if (o instanceof Long) {
      Long.toString((Long) o);
    }
    return null;
  }

  public static String[] evaluateTemplate(String template, ElasticEntryDto entry) {

    if (template == null)
      return new String[0];
    if ( !template.contains("#{"))
      return template.split("\\.");

    StringTokenizer st = new StringTokenizer(template, "}");
    StringBuilder sb = new StringBuilder();

    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      String[] parts = token.split(Pattern.quote("#{"));
      sb.append(parts[0]);
      Object tmp = getFieldValue(entry.getData(), parts[1].split("\\."));
      if (tmp != null) {
        sb.append(getObjectAsString(tmp));
      } else {
        return new String[0];
      }
    }
    return sb.toString().split("\\.");
  }

  public static short getTextColorForBackground(Color backGround) {
    //Convert to YIQ color space
    int yiq = ((backGround.getRed() * 299) + (backGround.getGreen() * 587) + (backGround.getBlue() * 114)) / 1000;
    return (yiq >= 128) ? HSSFColor.BLACK.index : HSSFColor.WHITE.index;
  }

  public static void setRowHeight(Row row, int lines, int maxLines) {
    if (lines <= 1)
      return;
    float h = row.getHeightInPoints();
    if (lines > maxLines)
      lines = maxLines;
    float newH = lines * 15;
    if (newH > h)
      row.setHeightInPoints(newH);
  }
}
