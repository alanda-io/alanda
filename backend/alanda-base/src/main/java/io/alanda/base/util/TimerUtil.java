/**
 * 
 */
package io.alanda.base.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author developer
 *
 */
public class TimerUtil {

  Map<String, Long> timerMap = new HashMap<>();

  Long time;

  String name = null;

  public static TimerUtil createStarted(String name) {
    TimerUtil tu = new TimerUtil();
    tu.start(name);
    return tu;
  }

  public void start(String name) {
    if (time != null)
      throw new IllegalArgumentException("Already started.");
    this.time = System.currentTimeMillis();
    this.name = name;
  }

  public void stop() {
    if (time == null)
      throw new IllegalArgumentException("Not started.");
    long duration = System.currentTimeMillis() - time;
    Long oldDuration = timerMap.get(name);
    if (oldDuration != null)
      duration = duration + oldDuration;
    timerMap.put(name, duration);
    this.time = null;
    this.name = null;
  }

  public void switchTo(String name) {
    stop();
    start(name);
  }

  public String toString() {
    return timerMap.toString();
  }

}
