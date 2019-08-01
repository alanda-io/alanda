package io.alanda.base.dao;

import java.util.List;

import io.alanda.base.entity.DocuConfig;
import io.alanda.base.entity.DocuConfigMapping;


public interface DocuConfigMappingDao extends Dao<DocuConfigMapping> {

  List<DocuConfigMapping> getByProjectTypeId(Long projectTypeId);

  DocuConfigMapping getByRefObjectType(String refObjectType);
  
  DocuConfig getByMappingName(String mappingName);

}
