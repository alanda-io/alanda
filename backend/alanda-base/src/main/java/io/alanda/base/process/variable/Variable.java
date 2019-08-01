package io.alanda.base.process.variable;

import java.io.Serializable;

public interface Variable<T extends Serializable> extends Serializable {

  public T get();

  public T getCached();

}
