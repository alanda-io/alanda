package io.alanda.base.dao;


public interface IdCounterDao {

  public long getNext(String prefix);
  
}
