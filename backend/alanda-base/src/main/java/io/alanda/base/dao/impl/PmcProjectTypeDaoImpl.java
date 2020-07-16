package io.alanda.base.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcProjectTypeDao;
import io.alanda.base.entity.PmcProjectType;

public class PmcProjectTypeDaoImpl 
  extends AbstractCrudDao<PmcProjectType> 
  implements PmcProjectTypeDao {

  private static final Logger log = LoggerFactory.getLogger(PmcProjectTypeDaoImpl.class);
  
  
  public PmcProjectTypeDaoImpl() {
    super();
  }

  public PmcProjectTypeDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }

  @Override
  public PmcProjectType getByIdName(String idName) {
    log.debug("Retrieving project type by idName {}", idName);

    return em
      .createQuery("select p FROM PmcProjectType p where p.idName = :idName", PmcProjectType.class)
      .setParameter("idName", idName)
      .getSingleResult();
  }
  
  @Override
  public List<PmcProjectType> searchByName(String searchTerm) {
    log.debug("Retrieving project types by searching for {}", searchTerm);

    return em
      .createQuery("Select p FROM PmcProjectType p where LOWER(p.name) like LOWER(:searchTerm)", PmcProjectType.class)
      .setParameter("searchTerm", searchTerm + "%")
      .getResultList();
  }
  
  @Override
  public List<PmcProjectType> searchByNameAndParentType(
    String searchTerm,
    Long projectParentGuid) {
    log.debug("Retrieving project types of parent with gid {}, searching for {}", projectParentGuid, searchTerm);
    return em
      .createQuery("SELECT child FROM PmcProjectType child, PmcProjectType parent WHERE " +
        "child MEMBER OF parent.allowedChildTypes AND parent.guid = :projectParentGuid AND " +
        "LOWER(child.name) LIKE LOWER(:searchTerm)", 
        PmcProjectType.class)
      .setParameter("searchTerm", searchTerm + "%")
      .setParameter("projectParentGuid", projectParentGuid)
      .getResultList();
  }
  
  @Override
  public List<PmcProjectType> getChildTypes(String idName) {
    log.debug("Retrieving children of project type with idName {}", idName);

    return em
        .createQuery("SELECT child FROM PmcProjectType child, PmcProjectType parent WHERE " +
          "child MEMBER OF parent.allowedChildTypes AND parent.idName = :idName", 
          PmcProjectType.class)
        .setParameter("idName", idName)
        .getResultList();
  }
  
  @Override
  public List<PmcProjectType> getParentTypes(String idName) {
    log.debug("Retrieving parents of project type with idName {}", idName);

    return em
        .createQuery("SELECT parent FROM PmcProjectType child, PmcProjectType parent WHERE " +
          "child MEMBER OF parent.allowedChildTypes AND child.idName = :idName", 
          PmcProjectType.class)
        .setParameter("idName", idName)
        .getResultList();
  }
  
}
