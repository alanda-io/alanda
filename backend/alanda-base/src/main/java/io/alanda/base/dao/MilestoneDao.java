package io.alanda.base.dao;

import io.alanda.base.entity.Milestone;



public interface MilestoneDao extends CrudDao<Milestone> {
  
  Milestone getMilestoneByIdName(String idName);

}
