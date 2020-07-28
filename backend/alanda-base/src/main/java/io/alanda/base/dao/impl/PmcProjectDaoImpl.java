package io.alanda.base.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcProjectDao;
import io.alanda.base.dto.RefObject;
import io.alanda.base.entity.PmcProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PmcProjectDaoImpl extends AbstractCrudDao<PmcProject> implements PmcProjectDao {
  private static final Logger log = LoggerFactory.getLogger(PmcProjectDaoImpl.class);

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
    log.debug("Retrieving project by id {}", projectId);

    return em
      .createQuery("select p FROM PmcProject p where p.projectId = :projectId", PmcProject.class)
      .setParameter("projectId", projectId)
      .getSingleResult();
  }

  @Override
  public List<PmcProject> getByCustomerProjectId(Long customerProjectId) {
    log.debug("Retrieving customer projects by id {}", customerProjectId);

    return em
      .createQuery("select p FROM PmcProject p where p.customerProjectId = :customerProjectId", PmcProject.class)
      .setParameter("customerProjectId", customerProjectId)
      .getResultList();
  }

  @Override
  public List<PmcProject> getAllActiveWithCustomerProject() {
    log.debug("Retrieving all active projects having customer projects");

    return em
      .createQuery("select p FROM PmcProject p where p.customerProjectId is not null and p.status=io.alanda.base.type.PmcProjectState.ACTIVE", PmcProject.class)
      .getResultList();
  }

  @Override
  public List<PmcProject> getProjectsByProjectTypeAndTags(Collection<String> types, Collection<String> tags) {
    log.debug("Retrieving all projects of types {}, having tags {}", types, tags);

    List<PmcProject> typeFiltered = getProjectsByProjectType(types);
    return filterProjectsByTag(typeFiltered, tags);
  }

  @Override
  public List<PmcProject> getProjectsByProjectType(Collection<String> types) {
    log.debug("Retrieving projects by types {}", types);

    return em
      .createQuery("SELECT p FROM PmcProject p WHERE p.pmcProjectType.idName IN :types", PmcProject.class)
      .setParameter("types", types)
      .getResultList();
  }

  @Override
  public List<PmcProject> getProjectsByProjectTags(Collection<String> tags) {
    log.debug("Retrieving projects by tags {}", tags);

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
    log.debug("Retrieving all child projects of project {}", parent);

    return em
      .createQuery("SELECT p FROM PmcProject p WHERE :parent MEMBER OF p.parents", PmcProject.class)
      .setParameter("parent", parent)
      .getResultList();
  }
  
  @Override
  public List<PmcProject> getParentProjects(PmcProject child) {
    log.debug("Retrieving all parent projects of child {}", child);

    return em
      .createQuery("SELECT p FROM PmcProject p WHERE :child MEMBER OF p.children", PmcProject.class)
      .setParameter("child", child)
      .getResultList();
  }

  @Override
  public List<PmcProject> getByTypeAndRefObject(String typeIdName, RefObject refObject) {
    log.debug("Retrieving projects by type {} and refObject {}", typeIdName, refObject);

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
    log.debug("Retrieving projects by type {}", projectTypeIdName);

    return em
        .createQuery("SELECT p FROM PmcProject p WHERE p.pmcProjectType.idName = :projectTypeIdName", PmcProject.class)
        .setParameter("projectTypeIdName", projectTypeIdName)
        .getResultList();    
  }

  @Override
  public List<PmcProject> getByTypeAndRefObjectId(String refObjectType, long refObjectId, Long projType) {
    log.debug("Retrieving projects by typeId {} with refObject {}@{}", projType, refObjectType, refObjectId);

    String query =  "SELECT p FROM PmcProject p WHERE " +
            "p.refObjectType = :refObjectType AND " +
            "p.refObjectId = :refObjectId";
    if(projType != null) {
      query += " AND p.pmcProjectType.guid = :projType";
    }
    TypedQuery typedQuery = em.createQuery(query, PmcProject.class)
        .setParameter("refObjectType", refObjectType)
        .setParameter("refObjectId", refObjectId);

    if(projType != null) {
      typedQuery.setParameter("projType", projType);
    }
    return typedQuery.getResultList();
  }

}
