package io.alanda.base.service.impl;

import io.alanda.base.service.AlandaSyncService;
import io.alanda.base.service.ElasticService;
import io.alanda.base.service.PmcTaskService;
import io.alanda.base.type.PmcProjectState;
import io.alanda.base.util.ElasticUtil;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
public class AlandaSyncServiceImpl implements AlandaSyncService {


  private static final Logger log = LoggerFactory.getLogger(AlandaSyncServiceImpl.class);

  @Inject
  private TaskService taskService;

  @Inject
  private ElasticService elasticService;

  @Inject
  private PmcTaskService pmcTaskService;

  @Override
  public void syncActiveTasks(int ttlInSeconds) {

    long startTime = System.currentTimeMillis();
    List<Task> camundaTasks = taskService.createTaskQuery().orderByTaskCreateTime().asc().list();
    Set<String> camundaTaskIds = new HashSet<>();
    for (Task t : camundaTasks) {
      camundaTaskIds.add(t.getId());
    }
    Map<String, Object> sortOptions = new HashMap<>();
    Map<String, Object> filterOptions = new HashMap<>();
    sortOptions.put("task.created", ElasticUtil.sort(1, SortOrder.ASC));
    filterOptions.put("task.state", ElasticUtil.raw(PmcProjectState.ACTIVE));
    filterOptions.put("task.actinst_type", ElasticUtil.raw("task"));

    Set<String> elasticTaskIds = new HashSet<>();
    int size = 100;
    int from = 0;
    while (size == 100) {
      SearchHits hits = elasticService.findTasks(filterOptions, sortOptions, from, size);
      from += size;
      size = 0;
      for (SearchHit hit : hits.getHits()) {
        size++;
        Map<String, Object> task = (Map<String, Object>) hit.getSourceAsMap().get("task");
        String taskId = (String) task.get("task_id");
        elasticTaskIds.add(taskId);
      }
    }

    Set<String> onlyElasticTaskIds = new HashSet<>();
    onlyElasticTaskIds.addAll(elasticTaskIds);
    onlyElasticTaskIds.removeAll(camundaTaskIds);

    log.info("Found " + elasticTaskIds.size() + " active tasks in Elastic task index.");
    for (String taskId : onlyElasticTaskIds) {
      log.warn("Task " + taskId + " has no active counterpart in Camunda -> deleting from Elastic index.");
      elasticService.deleteTask(taskId);
      if (ttlExceeded(startTime, ttlInSeconds)) {
        log.warn("Task syncing exceeded ttl of " + ttlInSeconds + " seconds. Exiting.");
        return;
      }
    }

    Set<String> onlyCamundaTaskIds = new HashSet<>();
    onlyCamundaTaskIds.addAll(camundaTaskIds);
    onlyCamundaTaskIds.removeAll(elasticTaskIds);

    log.info("Found " + camundaTaskIds.size() + " active tasks in Camunda.");
    for (String taskId : onlyCamundaTaskIds) {
      log.warn("Task " + taskId + " has no active counterpart in Elastic -> syncing to Elastic index.");
      pmcTaskService.updateTask(taskId);
      if (ttlExceeded(startTime, ttlInSeconds)) {
        log.warn("Task syncing exceeded ttl of " + ttlInSeconds + " seconds. Exiting.");
        return;
      }
    }

    long endTime = System.currentTimeMillis();
    long timeInSeconds = Math.round((endTime - startTime) / 1000);
    log.info("Time to sync active tasks: " + timeInSeconds + " seconds.");
  }

  private boolean ttlExceeded(long startTime, int ttlInSeconds) {
    long actTime = System.currentTimeMillis();
    long timeInSeconds = Math.round((actTime - startTime) / 1000);
    return timeInSeconds > ttlInSeconds;
  }

}
