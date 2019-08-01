package io.alanda.base.dao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import io.alanda.base.dao.AbstractDao;
import io.alanda.base.dao.DocuConfigMappingDao;
import io.alanda.base.entity.DocuConfig;
import io.alanda.base.entity.DocuConfigMapping;

@Stateless
public class DocuConfigMappingDaoImpl extends AbstractDao<DocuConfigMapping> implements DocuConfigMappingDao {

  @PersistenceContext(name = "pmcDB", unitName = "pmcDB")
  private EntityManager em;

  public DocuConfigMappingDaoImpl() {

  }

  public DocuConfigMappingDaoImpl(EntityManager em) {
    this.em = em;
  }

  @Override
  protected Class<DocuConfigMapping> getEntityClass() {
    return DocuConfigMapping.class;
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @Override
  public List<DocuConfigMapping> getByProjectTypeId(Long projectTypeId) {
    return em
      .createNamedQuery("DocuConfigMapping.getByProjectTypeId", DocuConfigMapping.class)
      .setParameter("projectTypeId", projectTypeId)
      .getResultList();
  }

  @Override
  public DocuConfigMapping getByRefObjectType(String refObjectType) {
    return em
      .createNamedQuery("DocuConfigMapping.getByRefObjectType", DocuConfigMapping.class)
      .setParameter("refObjectType", refObjectType)
      .getSingleResult();
  }
  
  @Override
  public DocuConfig getByMappingName(String mappingName) {
    return em.createNamedQuery("DocuConfig.getByMappingName", DocuConfig.class).setParameter("mappingName", mappingName).getSingleResult();
  }
}
