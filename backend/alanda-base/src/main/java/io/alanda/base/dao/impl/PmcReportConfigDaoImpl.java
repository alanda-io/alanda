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

/**
 *
 * @author developer
 */
public class PmcReportConfigDaoImpl extends AbstractCrudDao<PmcReportConfig> implements PmcReportConfigDao {

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
        return em
                .createQuery("select r FROM PmcReportConfig r where r.reportName = :reportName", PmcReportConfig.class)
                .setParameter("reportName", reportName)
                .getSingleResult();
    }

    @Override
    public List<PmcReportConfig> getReportBySendtime(String sendTime) {
        return em
                .createQuery("select r FROM PmcReportConfig r where r.sendTime = :sendTime", PmcReportConfig.class)
                .setParameter("sendTime", sendTime)
                .getResultList();
    }

    @Override
    public List<PmcReportConfig> getReportsForDay(String day) {
      return em.createQuery("select r FROM PmcReportConfig r where r.sendTime like :day", PmcReportConfig.class)
          .setParameter("day", "%" + day + "%")
          .getResultList();
    }
    
}
