/**
 * 
 */
package io.alanda.base.reporting.db;

/**
 * @author jlo
 */
public class FieldHeader {

  private String name;

  private Class<?> clazz;

  /**
   * 
   */
  public FieldHeader(String name, Class<?> clazz) {
    this.name = name;
    this.clazz = clazz;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }

  @Override
  public String toString() {
    return "FieldHeader [name=" + name + ", type=" + clazz + "]";
  }

}
