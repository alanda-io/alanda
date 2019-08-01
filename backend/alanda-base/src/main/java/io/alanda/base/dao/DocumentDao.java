package io.alanda.base.dao;

import java.util.Collection;

import io.alanda.base.entity.Document;


public interface DocumentDao extends CrudDao<Document> {
  
  public Document getByPathAndFilename(String path, String fileName);
  
  public Collection<Document> getByPath(String path);

}
