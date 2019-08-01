package io.alanda.base.dao;

import java.util.List;

import io.alanda.base.entity.PmcProject;
import io.alanda.base.entity.PmcProperty;


public interface PmcPropertyDao extends CrudDao<PmcProperty> {

  PmcProperty getProperty(String key, Long entityId, String entityType, PmcProject pmcProject);
  
  PmcProperty getProperty(String key, Long entityId, String entityType, Long pmcProjectGuid);
  
  List<PmcProperty> getPropertiesLike(String keyLike, Long entityId, String entityType, Long pmcProjectGuid);

  List<PmcProperty> getPropertiesLikeWithValue(String keyLike, String value, String valueType, Long entityId, String entityType, Long pmcProjectGuid);

  List<PmcProperty> getPropertiesForProject(Long pmcProjectGuid);
  
  int deleteProperty(String key, Long entityId, String entityType, Long pmcProjectGuid);

  int deletePropertyLike(String keyLike, Long entityId, String entityType, Long pmcProjectGuid);

}
