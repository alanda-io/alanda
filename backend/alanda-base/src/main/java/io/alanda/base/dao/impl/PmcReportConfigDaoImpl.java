/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.dao.impl;

import java.util.List;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.entity.PmcReportConfig;

import javax.persistence.EntityManager;

import io.alanda.base.dao.PmcReportConfigDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author developer
 */
public class PmcReportConfigDaoImpl extends AbstractCrudDao<PmcReportConfig> implements PmcReportConfigDao {
    private static final Logger log = LoggerFactory.getLogger(PmcReportConfigDaoImpl.class);

    public PmcReportConfigDaoImpl() {
    }

    public PmcReportConfigDaoImpl(EntityManager em) {
        super(em);
    }

    @Override
    public EntityManager getEntityManager() {
        return em; 
    }
    
    
    @Override
    public PmcReportConfig getReportByName(String reportName) {
        log.debug("Retrieving report by name {}", reportName);

        return em
                .createQuery("select r FROM PmcReportConfig r where r.reportName = :reportName", PmcReportConfig.class)
                .setParameter("reportName", reportName)
                .getSingleResult();
    }

    @Override
    public List<PmcReportConfig> getReportBySendtime(String sendTime) {
        log.debug("Retrieving report sent at time {}", sendTime);

        return em
                .createQuery("select r FROM PmcReportConfig r where r.sendTime = :sendTime", PmcReportConfig.class)
                .setParameter("sendTime", sendTime)
                .getResultList();
    }

    @Override
    public List<PmcReportConfig> getReportsForDay(String day) {
      log.debug("Retrieving reports sent at day {}", day);

      return em.createQuery("select r FROM PmcReportConfig r where r.sendTime like :day", PmcReportConfig.class)
          .setParameter("day", "%" + day + "%")
          .getResultList();
    }
    
}
