package io.alanda.base.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractManualMapper<E, D> implements SmMapper<E, D> {

  @Override
  public List<E> mapCollectionFromDto(Collection<D> dtos) {
    if (dtos == null) {
      return null;
    }
    List<E> collection = new ArrayList<>();
    for (D dto : dtos) {
      collection.add(mapEntityFromDto(dto));
    }
    return collection;
  }

  /**
   * Maps a Collection of {@link SmCluster} to a Collection of {@link SmClusterDto}
   * 
   * @param entities
   * @return
   */
  @Override
  public List<D> mapCollectionToDto(Collection<E> entities) {
    if (entities == null) {
      return null;
    }
    List<D> collection = new ArrayList<>();
    for (E entity : entities) {
      collection.add(mapEntityToDto(entity));
    }
    return collection;
  }

}
