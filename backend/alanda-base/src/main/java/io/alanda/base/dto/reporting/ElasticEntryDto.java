/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.dto.reporting;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author developer
 */
public class ElasticEntryDto implements Serializable {

  private static final long serialVersionUID = -5376707598709550330L;
  
  Map<String, Object> data;

  Map<String, Map<String,Object>> lastUT = new HashMap<>();
  
  public final String LAST_USER_TASK = "lastUT";
  
  public ElasticEntryDto() {
  }

  public ElasticEntryDto(Map<String, Object> data) {
    this.data = data;
  }


  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }
    
  public void updateLastUT(Map<String, Object> activity) {
    Map<String, Object> last = lastUT.get(LAST_USER_TASK);
    if (last == null) {
      if (activity.get("startTime") != null) {
        lastUT.put(LAST_USER_TASK, activity);
      }
    } else {
      if (activity.get("startTime") != null) { //last.get("startTime") cannot be null - see check above
        if ((Long) activity.get("startTime") > (Long) last.get("startTime")) {
          lastUT.put(LAST_USER_TASK, activity);
        }
      }
    }
  }
  
  public Map<String, Object> getLastUserTask() {
    return lastUT.get(LAST_USER_TASK);
  }
  
}
