package io.alanda.base.dao;

import java.util.Collection;
import java.util.List;

import io.alanda.base.dto.RefObject;
import io.alanda.base.entity.PmcProject;


public interface PmcProjectDao extends CrudDao<PmcProject> {
  
  PmcProject getByProjectId(String projectId);
  
  List<PmcProject> getProjectsByProjectTypeAndTags(
    Collection<String> types,
    Collection<String> tags);

  List<PmcProject> getProjectsByProjectType(
    Collection<String> types);

  List<PmcProject> getProjectsByProjectTags(
    Collection<String> tags);
  
  List<PmcProject> getChildProjects(
    PmcProject parent);
  
  List<PmcProject> getParentProjects(PmcProject child);
  
  List<PmcProject> getByTypeAndRefObject(
    String typeIdName, RefObject refObject);

  List<PmcProject> getByCustomerProjectId(Long customerProjectId);

  List<PmcProject> getAllActiveWithCustomerProject();
  
  List<PmcProject> getAllWithProjectType(String projectTypeIdName);

}
