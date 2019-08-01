package io.alanda.base.dao;

import java.util.List;

import io.alanda.base.entity.PmcProjectType;


public interface PmcProjectTypeDao extends CrudDao<PmcProjectType> {
  
  public PmcProjectType getByIdName(String idName);
  
  public List<PmcProjectType> searchByName(String searchTerm); 

  public List<PmcProjectType> searchByNameAndParentType(
    String searchTerm,
    Long projectParentGuid);
  
  List<PmcProjectType> getChildTypes(String idName);
  
  List<PmcProjectType> getParentTypes(String idName);

}
