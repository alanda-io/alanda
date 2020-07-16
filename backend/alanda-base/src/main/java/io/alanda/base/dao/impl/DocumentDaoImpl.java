package io.alanda.base.dao.impl;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.DocumentDao;
import io.alanda.base.entity.Document;


public class DocumentDaoImpl extends AbstractCrudDao<Document> implements DocumentDao {

  private static final Logger log = LoggerFactory.getLogger(DocumentDaoImpl.class);
  
  public DocumentDaoImpl() {
    super();
  }

  public DocumentDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }
  
  @Override
  public Document getByPathAndFilename(String path, String fileName) {
    log.debug("Retrieving document with name {} and path {}", fileName, path);

    try {
      return em
           .createNamedQuery("Document.getByPathAndFileName", Document.class)
           .setParameter("path", path)
           .setParameter("fileName", fileName)
           .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }
  
  @Override
  public Collection<Document> getByPath(String path) {
    log.debug("Retrieving document for path {}", path);

    return em
         .createNamedQuery("Document.getByPath", Document.class)
         .setParameter("path", path)
         .getResultList(); 
  }
}
