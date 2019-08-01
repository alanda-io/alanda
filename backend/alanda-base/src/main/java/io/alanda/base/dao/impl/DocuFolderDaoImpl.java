package io.alanda.base.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import io.alanda.base.dao.AbstractDao;
import io.alanda.base.dao.DocuFolderDao;
import io.alanda.base.entity.DocuFolder;

public class DocuFolderDaoImpl extends AbstractDao<DocuFolder> implements DocuFolderDao {

  @PersistenceContext(name = "pmcDB", unitName = "pmcDB")
  private EntityManager em;

  public DocuFolderDaoImpl() {

  }

  public DocuFolderDaoImpl(EntityManager em) {
    this.em = em;
  }

  @Override
  protected Class<DocuFolder> getEntityClass() {
    return DocuFolder.class;
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

}
