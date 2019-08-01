package io.alanda.base.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import io.alanda.base.entity.PmcProjectMilestone;

public interface PmcProjectMilestoneDao extends CrudDao<PmcProjectMilestone> {

  List<PmcProjectMilestone> getMilestonesPerProject(Long projectId);

  PmcProjectMilestone getByProjectAndIdName(String projectIdName, String msIdName);

  PmcProjectMilestone getByProjectAndIdName(Long pmcProjectGuid, String msIdName);

  Collection<PmcProjectMilestone> getModifiedMilestones(Date modifiedSince);
  
  PmcProjectMilestone getMilestoneByIdNameAndAct(String idName, Date act);

}
