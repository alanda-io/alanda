package io.alanda.base.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcProjectMilestoneDao;
import io.alanda.base.entity.PmcProjectMilestone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PmcProjectMilestoneDaoImpl extends AbstractCrudDao<PmcProjectMilestone> implements PmcProjectMilestoneDao {
  private static final Logger log = LoggerFactory.getLogger(PmcProjectMilestoneDaoImpl.class);

  public PmcProjectMilestoneDaoImpl() {
    super();
  }

  public PmcProjectMilestoneDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }

  @Override
  protected Class<PmcProjectMilestone> getEntityClass() {
    return PmcProjectMilestone.class;
  }

  @Override
  public List<PmcProjectMilestone> getMilestonesPerProject(Long pmcProjectGuid) {
    log.debug("Retrieving milestones for project with id {}", pmcProjectGuid);

    List<PmcProjectMilestone> projectMilestones;
    projectMilestones = em
      .createQuery("select pm from PmcProjectMilestone pm where pm.project.guid = :pmcProjectGuid", PmcProjectMilestone.class)
      .setParameter("pmcProjectGuid", pmcProjectGuid)
      .getResultList();
    return projectMilestones;
  }

  @Override
  public PmcProjectMilestone getByProjectAndIdName(String projectIdName, String msIdName) {
    log.debug("Retrieving milestone with idName {} for project with idName {}", msIdName, projectIdName);

    List<PmcProjectMilestone> projectMilestones = em
      .createQuery(
        "select pm from PmcProjectMilestone pm where " + "pm.project.projectId = :projectId AND " + "pm.milestone.idName = :msIdName",
        PmcProjectMilestone.class)
      .setParameter("projectId", projectIdName)
      .setParameter("msIdName", msIdName)
      .getResultList();
    if (projectMilestones.size() > 1)
      throw new IllegalStateException("multiple project milestones found for project=" + projectIdName + " " + "and msIdName=" + msIdName);
    if (projectMilestones.size() == 1) {
      return projectMilestones.get(0);
    } else {
      return null;
    }
  }

  @Override
  public PmcProjectMilestone getByProjectAndIdName(Long pmcProjectGuid, String msIdName) {
    log.debug("Retrieving milestone with idName {} for project with guid {}", msIdName, pmcProjectGuid);

    List<PmcProjectMilestone> projectMilestones = em
      .createQuery(
        "select pm from PmcProjectMilestone pm where " + "pm.project.guid = :pmcProjectGuid AND " + "pm.milestone.idName = :msIdName",
        PmcProjectMilestone.class)
      .setParameter("pmcProjectGuid", pmcProjectGuid)
      .setParameter("msIdName", msIdName)
      .getResultList();
    if (projectMilestones.size() > 1)
      throw new IllegalStateException("multiple project milestones found for project=" + pmcProjectGuid + " " + "and msIdName=" + msIdName);
    if (projectMilestones.size() == 1) {
      return projectMilestones.get(0);
    } else {
      return null;
    }
  }

  @Override
  public List<PmcProjectMilestone> getModifiedMilestones(Date modifiedSince) {
    log.debug("Retrieving all milestones modified since {}", modifiedSince);

    List<PmcProjectMilestone> projectMilestones;
    projectMilestones = em
      .createQuery("select pm from PmcProjectMilestone pm where pm.updateDate >= :modifiedSince", PmcProjectMilestone.class)
      .setParameter("modifiedSince", modifiedSince)
      .getResultList();
    return projectMilestones;
  }

  @Override
  public PmcProjectMilestone getMilestoneByIdNameAndAct(String idName, Date act) {
    log.debug("Retrieving milestone by id name {} and actual date at {}", idName, act);

    List<PmcProjectMilestone> projectMilestones = em
      .createQuery("select pm from PmcProjectMilestone pm where pm.milestone.idName = :idName and pm.act = :act", PmcProjectMilestone.class)
      .setParameter("idName", idName)
      .setParameter("act", act)
      .getResultList();
    if (projectMilestones.size() > 1) {
      throw new RuntimeException("there is more then one milestone for project = " + idName);
    } else if (projectMilestones.size() == 1) {
      return projectMilestones.get(0);
    }
    return null;
  }
}
