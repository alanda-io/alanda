package io.alanda.base.mapper;

import java.util.Collection;
import java.util.List;

public interface SmMapper<E, D> {

  /**
   * Maps a DTO to a Entity
   * 
   * @param dto
   * @return
   */
  public E mapEntityFromDto(D dto);

  /**
   * Maps a Entity to a DTO
   * 
   * @param cluster
   * @return
   */
  public D mapEntityToDto(E entity);

  /**
   * Maps a Collection of DTOs to a Collection of Entities
   * 
   * @param dtos
   * @return
   */
  public List<E> mapCollectionFromDto(Collection<D> dtos);

  /**
   * Maps a Collection of Entities to a Collection of DTOs
   * 
   * @param entities
   * @return
   */
  public List<D> mapCollectionToDto(Collection<E> entities);

}
