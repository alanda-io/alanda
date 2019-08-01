/**
 * 
 */
package io.alanda.base.util.cache;

import java.io.Serializable;

import com.google.common.cache.CacheStats;

/**
 * @author jlo
 */
public class CacheStatsDto implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -758925400502844087L;

  private final long hitCount;

  private final long missCount;

  private final long loadSuccessCount;

  private final long loadExceptionCount;

  private final long totalLoadTime;

  private final long evictionCount;

  private final double hitRate;

  private final double averageLoadPenalty;

  private final double loadExceptionRate;

  private final double missRate;

  /**
   * 
   */
  public CacheStatsDto(CacheStats cs) {
    this.hitCount = cs.hitCount();
    this.missCount = cs.missCount();

    this.loadSuccessCount = cs.loadSuccessCount();
    this.loadExceptionCount = cs.loadExceptionCount();
    this.totalLoadTime = cs.totalLoadTime();
    this.evictionCount = cs.evictionCount();
    
    this.hitRate = cs.hitRate();

    this.averageLoadPenalty = cs.averageLoadPenalty();
    this.loadExceptionRate = cs.loadExceptionRate();
    this.missRate = cs.missRate();

  }

  public long getHitCount() {
    return hitCount;
  }

  public long getMissCount() {
    return missCount;
  }

  public long getLoadSuccessCount() {
    return loadSuccessCount;
  }

  public long getLoadExceptionCount() {
    return loadExceptionCount;
  }

  public long getTotalLoadTime() {
    return totalLoadTime;
  }

  public long getEvictionCount() {
    return evictionCount;
  }

  public double getHitRate() {
    return hitRate;
  }

  public double getAverageLoadPenalty() {
    return averageLoadPenalty;
  }

  public double getLoadExceptionRate() {
    return loadExceptionRate;
  }

  public double getMissRate() {
    return missRate;
  }

}
