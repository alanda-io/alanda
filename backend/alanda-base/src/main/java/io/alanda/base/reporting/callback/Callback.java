/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.reporting.callback;

import java.util.Map;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;

/**
 *
 * @author developer
 */
public interface Callback {
  
  public String getName();
  
  public void execute(Map<String, Object> params, ElasticEntryDto entry, Report report, Integer rowIndex, Integer colIndex); 
  
}
