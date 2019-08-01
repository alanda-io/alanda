package io.alanda.base.dao;

import java.util.List;

import io.alanda.base.entity.PmcComment;

public interface PmcCommentDao extends Dao<PmcComment> {
  
  List<PmcComment> getAllForProcessInstanceId(String processInstanceId);
  
  void insert(PmcComment pmcComment);

  List<PmcComment> getAllForProcessInstanceIdAndRefObjectId(String processInstanceId, long refObjectId);

}
