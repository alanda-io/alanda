package io.alanda.base.dao;

import java.util.Collection;

public interface Dao<T> {

  Collection<T> getAll();

  T getById(Long id);

  T getReference(Long id);
}
