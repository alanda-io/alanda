/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.reporting.postprocess;


import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.search.SearchHit;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.ColumnType;

/**
 *
 * @author developer
 */
public class SubProcessPostProcessor extends DefaultPostProcessor {

  final public static String NAME = "subProcess";
  
  @Override
  public String getName() {
    return NAME; 
  }

  @Override
  public void processHit(ElasticEntryDto entry, SearchHit[] hits, Map<String, Object> reportContext) {    
    Map<String, Object> data  = entry.getData();
    Map<String, Object> preparedSubs = (Map<String, Object>) data.get(ColumnType.PROCESS.getLabel());
    if (preparedSubs == null) preparedSubs = new HashMap<>();
    for (SearchHit process : hits) {
      //TODO: some sort logic here (date,...)
      Map<String, Object> proc = process.getSourceAsMap();
      sortActivities(entry, proc);
      sortVariables(entry, proc);
      //inject milestones to subprocesses
      proc.put(ColumnType.MILESTONE.getLabel(), data.get(ColumnType.MILESTONE.getLabel()));
      preparedSubs.put((String) proc.get("processDefinitionKey"), proc);
    }
    data.put(ColumnType.PROCESS.getLabel(), preparedSubs);
    Map<String, Object> proj = (Map<String, Object>) data.get("project");
    proj.put(entry.LAST_USER_TASK,entry.getLastUserTask());
  }  
  
}
