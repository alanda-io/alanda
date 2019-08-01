/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.dao;

import java.util.List;

import io.alanda.base.entity.PmcReportConfig;

/**
 *
 * @author developer
 */
public interface PmcReportConfigDao extends CrudDao<PmcReportConfig> {
    
    public PmcReportConfig getReportByName(String reportName);
    
    public List<PmcReportConfig> getReportBySendtime(String sendTime);
    
    public List<PmcReportConfig> getReportsForDay(String day);
    
}
