package io.alanda.base.reporting.postprocess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.elasticsearch.search.SearchHit;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.ColumnType;
import io.alanda.base.type.ProcessState;

/**
 *
 * @author developer
 */
public class DefaultPostProcessor implements SearchHitPostProcessor {
  
  final public static String NAME = "default";
  
  final public static String COUNT = "subProcCount";
  final public static String CREATEDCOUNT = "created";
  final public static String CLOSEDCOUNT = "closed";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public void processHit(ElasticEntryDto hit, SearchHit[] hits, Map<String, Object> reportContext) {
    sortActivities(hit, null);
    sortVariables(hit, null);
    sortMilestones(hit);
    sortPmcMilestones(hit);
    countSubProcesses(hit);
  }

  @Override
  public List<ElasticEntryDto> flattenEntries(ElasticEntryDto hit, Map<String, Object> reportContext) {
    return Collections.singletonList(hit);
  }

  public void sortActivities(ElasticEntryDto entry, Map<String, Object> data) {
    Map<String, Object> sortedActivities = new HashMap<>();
    if (data == null) {
      data = entry.getData();
    }
    List<Map<String, Object>> activities = (List<Map<String, Object>>) data.get("activities");
    if (activities != null) {
      for (Map<String, Object> activity : activities) {
        //TODO: some sort logic here (date,...)
        sortedActivities.put((String) activity.get("activityId"), activity);
        if ("userTask".equals(activity.get("activityType"))) {
          entry.updateLastUT(activity);
        }
      }
    }
    data.put(ColumnType.ACTIVITY.getLabel(), sortedActivities);
    Map<String, Object> proj = (Map<String, Object>) entry.getData().get("project");
    proj.put(entry.LAST_USER_TASK,entry.getLastUserTask());
  }
  
  public void sortVariables(ElasticEntryDto entry, Map<String, Object> data) {
    Map<String, Object> sortedVariables = new HashMap<>();
        if (data == null) {
      data = entry.getData();
    }

    List<Map<String, Object>> variables = (List<Map<String, Object>>) data.get("variables");
    if (variables != null) {
      for (Map<String, Object> variable : variables) {
        //TODO: some sort logic here (date,...)
        sortedVariables.put((String) variable.get("varName"), variable);
      }
    }
    data.put(ColumnType.VARIABLE.getLabel(), sortedVariables);
  }
  
  public void sortMilestones(ElasticEntryDto entry) {
    Map<String, Object> sortedMilestones = new HashMap<>();
    Map<String, Object> data = entry.getData();
    Map<String, Object> customerProject = (Map<String, Object>) data.get("customerProject");
    if (customerProject != null) { //can happen before sync job
      List<Map<String, Object>> milestones = (List<Map<String, Object>>) customerProject.get("milestones");
      if (milestones != null) {
        for (Map<String, Object> milestone : milestones) {
          //TODO: some sort logic here (date,...)
          sortedMilestones.put((String) milestone.get("msName"), milestone);
        }
      }
    }
    Map<String, Object> refObject = (Map<String, Object>) data.get("refObject");
    if (refObject != null) { //can happen before sync job
      List<Map<String, Object>> milestones = (List<Map<String, Object>>) refObject.get("milestones");
      if (milestones != null) {
        for (Map<String, Object> milestone : milestones) {
          //TODO: some sort logic here (date,...)
          sortedMilestones.put((String) milestone.get("msName"), milestone);
        }
      }
    }
    data.put(ColumnType.MILESTONE.getLabel(), sortedMilestones);
  }
  
  public void sortPmcMilestones(ElasticEntryDto entry) {
    Map<String, Object> sortedMilestones = new HashMap<>();
    Map<String, Object> data = entry.getData();
    Map<String, Object> project = (Map<String, Object>) data.get("project");
    List<Map<String, Object>> milestones = (List<Map<String, Object>>) project.get("milestones");
    List<Map<String, Object>> history = (List<Map<String, Object>>) project.get("history");
    Map<String, List<Map<String, Object>>> historyMap = new HashMap<>();
    if (history != null) {
      for (Map<String, Object> hItem : history) {
        if (Objects.equals(hItem.get("type"), "Milestone")) {
          String key = (String) hItem.get("fieldRef");
          if (key != null) {
            key = key.replace("project.milestone.", "");
            if (historyMap.get(key) == null) {
              historyMap.put(key, new ArrayList<Map<String, Object>>());
            }
            historyMap.get(key).add(hItem);
          }
        }
      }
    }
    if (milestones != null) {
      for (Map<String, Object> milestone : milestones) {
        //TODO: some sort logic here (date,...)
        String msIdName = (String) milestone.get("idName");
        Map<String, Object> msHistory = new HashMap<>();
        addMsHistoryEntry(msIdName,  "fc", historyMap, msHistory);
        addMsHistoryEntry(msIdName, "act", historyMap, msHistory);
        addMsHistoryEntry(msIdName, "baseline", historyMap, msHistory);
        milestone.put("history", msHistory);
        sortedMilestones.put(msIdName, milestone);
      }
    }
    data.put(ColumnType.PMCMILESTONE.getLabel(), sortedMilestones);
  }

  private void addMsHistoryEntry(String msIdName, String type, Map<String, List<Map<String, Object>>> historyMap, Map<String, Object> result) {
    String key = msIdName + "." + type;
    if (historyMap.containsKey(key)) {
      result.put(type, historyMap.get(key));
    }
  }
  
  public void countSubProcesses(ElasticEntryDto entry) {
    Map<String, Map<String, Integer>> subCounters = new HashMap<>();
    Map<String, Object> data = entry.getData();
    Map<String, Object> project = (Map<String, Object>) data.get("project");
    List<Map<String, Object>> processes = (List<Map<String, Object>>) project.get("processes");
    if (processes != null) {
      for (Map<String, Object> proc : processes) {
        String procDefKey = (String) proc.get("processKey");
        String procStatus = (String) proc.get("status");
        if ((!ProcessState.CANCELED.name().equals(procStatus)) && (!ProcessState.DELETED.name().equals(procStatus))) {
          addCount(subCounters, procDefKey, CREATEDCOUNT);
        }
        if (ProcessState.COMPLETED.name().equals(procStatus)) {
          addCount(subCounters, procDefKey, CLOSEDCOUNT);
        }
      }
    }
    data.put(COUNT, subCounters);
  }
  
  private void addCount(Map<String, Map<String, Integer>> map, String procDefKey, String counter) {
    Map<String, Integer> counters = map.get(procDefKey);
    if (counters == null) {
      counters = new HashMap<>();
      map.put(procDefKey, counters);
    }
    Integer cnt = counters.get(counter);
    if (cnt == null) cnt = 0;
    counters.put(counter, ++cnt);
  }
  
}
