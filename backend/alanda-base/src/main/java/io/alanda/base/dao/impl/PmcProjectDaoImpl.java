package io.alanda.base.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcProjectDao;
import io.alanda.base.dto.RefObject;
import io.alanda.base.entity.PmcProject;

public class PmcProjectDaoImpl extends AbstractCrudDao<PmcProject> implements PmcProjectDao {

  public PmcProjectDaoImpl() {
    super();
  }

  public PmcProjectDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }

  @Override
  public PmcProject getByProjectId(String projectId) {
    return em
      .createQuery("select p FROM PmcProject p where p.projectId = :projectId", PmcProject.class)
      .setParameter("projectId", projectId)
      .getSingleResult();
  }

  @Override
  public List<PmcProject> getByCustomerProjectId(Long customerProjectId) {
    return em
      .createQuery("select p FROM PmcProject p where p.customerProjectId = :customerProjectId", PmcProject.class)
      .setParameter("customerProjectId", customerProjectId)
      .getResultList();
  }

  @Override
  public List<PmcProject> getAllActiveWithCustomerProject() {
    return em
      .createQuery("select p FROM PmcProject p where p.customerProjectId is not null and p.status=com.bpmasters.pmc.base.type.PmcProjectState.ACTIVE", PmcProject.class)   
      .getResultList();
  }

  @Override
  public List<PmcProject> getProjectsByProjectTypeAndTags(Collection<String> types, Collection<String> tags) {
    List<PmcProject> typeFiltered = getProjectsByProjectType(types);
    return filterProjectsByTag(typeFiltered, tags);
  }

  @Override
  public List<PmcProject> getProjectsByProjectType(Collection<String> types) {
    return em
      .createQuery("SELECT p FROM PmcProject p WHERE p.pmcProjectType.idName IN :types", PmcProject.class)
      .setParameter("types", types)
      .getResultList();
  }

  @Override
  public List<PmcProject> getProjectsByProjectTags(Collection<String> tags) {

    // ToDo: make JPA query if possible

    if (tags == null || tags.size() == 0)
      throw new IllegalArgumentException("Please provide tags to filter PmcProjects!");
    Collection<PmcProject> allProjects = this.getAll();
    return filterProjectsByTag(allProjects, tags);
  }

  private List<PmcProject> filterProjectsByTag(Collection<PmcProject> projects, Collection<String> tags) {

    List<PmcProject> filteredProject = new ArrayList<PmcProject>();
    for (PmcProject p : projects) {
      if (p.getTag() != null) {
        for (int i = 0; i < p.getTag().length; i++ )
          if (tags.contains(p.getTag()[i])) {
            filteredProject.add(p);
            break;
          }
      }
    }
    return filteredProject;
  }

  @Override
  public List<PmcProject> getChildProjects(PmcProject parent) {
    return em
      .createQuery("SELECT p FROM PmcProject p WHERE :parent MEMBER OF p.parents", PmcProject.class)
      .setParameter("parent", parent)
      .getResultList();
  }
  
  @Override
  public List<PmcProject> getParentProjects(PmcProject child) {
    return em
      .createQuery("SELECT p FROM PmcProject p WHERE :child MEMBER OF p.children", PmcProject.class)
      .setParameter("child", child)
      .getResultList();
  }

  @Override
  public List<PmcProject> getByTypeAndRefObject(String typeIdName, RefObject refObject) {
    return em
      .createQuery("SELECT p FROM PmcProject p WHERE " +
          "p.pmcProjectType.idName = :typeIdName AND " +
          "p.refObjectId = :refObjectId AND " +
        "p.refObjectType = :refObjectType", PmcProject.class)
      .setParameter("typeIdName", typeIdName)
      .setParameter("refObjectId", refObject.getRefObjectId())
      .setParameter("refObjectType", refObject.getRefObjectType())
      .getResultList();
  }
  
  @Override
  public List<PmcProject> getAllWithProjectType(String projectTypeIdName) {
    return em
        .createQuery("SELECT p FROM PmcProject p WHERE p.pmcProjectType.idName = :projectTypeIdName", PmcProject.class)
        .setParameter("projectTypeIdName", projectTypeIdName)
        .getResultList();    
  }

}
