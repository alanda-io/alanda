package io.alanda.base.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PMC_IDCOUNTER")
public class IdCounter {

  @Id
  private String prefix;

  private long currentNumber;

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public long getCurrentNumber() {
    return currentNumber;
  }

  public void setCurrentNumber(long number) {
    this.currentNumber = number;
  }

}
