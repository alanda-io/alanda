/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.reporting;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;

/**
 * @author developer
 */
public class ReportContext {

  private Map<String, Object> context;

  private Map<String, XSSFCellStyle> styleCache;

  public ReportContext() {
    this.context = new HashMap<>();
    this.styleCache = new HashMap<>();
  }

  public Map<String, XSSFCellStyle> getStyleCache() {
    return styleCache;
  }

  public Map<String, Object> getContext() {
    return context;
  }

  public void put(String[] fieldPath, Object value) {
    if (fieldPath.length > 0) {
      Map<String, Object> tmp = context;
      for (int i = 1; i < fieldPath.length; i++ ) {
        Map<String, Object> map = (Map<String, Object>) tmp.get(fieldPath[i - 1]);
        if (map == null) {
          map = new HashMap<>();
          tmp.put(fieldPath[i - 1], map);
        }
        tmp = map;
      }
      tmp.put(fieldPath[fieldPath.length - 1], value);
    }
  }

  public Object get(String fieldPath[]) {
    if (fieldPath.length == 0)
      return null;
    return ReportUtils.getFieldValue(this.context, fieldPath);
  }

}
