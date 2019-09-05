package io.alanda.base.service;

import java.util.List;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;

import io.alanda.base.dto.ElasticProcessHitDto;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.PmcProjectProcessDto;
import io.alanda.base.dto.PmcTaskDto;
import io.alanda.base.service.cdi.ElasticBulkUpdate;
import io.alanda.base.service.cdi.ElasticUpdate;

public interface ElasticService {



  void addProcess(String processInstanceId, PmcProjectProcessDto process, PmcProjectDto project, boolean updatedRelated);

  void updateProcessInfo(String processInstanceId, PmcProjectProcessDto process);

  void updateRefObject(String processInstanceId, String refObjectType, Long refObjectId);

  SearchHit[] findProcessesForProject(String projectId);

  SearchHit[] findByProjectType(String projectType);

  SearchHit[] findSubProcessesForProject(Integer projectGuid);

  SearchHit[] findByTemplate(String query, Map<String, Object> params, String[] fields, int from, int size, boolean fetchSource);

  SearchHit[] findByTemplateFromTaskIndex(
      String query,
      Map<String, Object> params,
      String[] fields,
      int from,
      int size,
      boolean fetchSource);

  SearchHits findProjects(Map<String, Object> filterParams, Map<String, Object> sortParams, int from, int size);

  List<ElasticProcessHitDto> findProjectsAsElasticDto(Map<String, Object> filterParams, Map<String, Object> sortParams, int from, int size);

  SearchHits findProjects(
      Map<String, Object> filterParams,
      Map<String, Object> sortParams,
      int from,
      int size,
      String[] sourceIncludes,
      String[] sourceExcludes);

  SearchHits findTasks(Map<String, Object> filterParams, Map<String, Object> sortParams, int from, int size);

  void synchData(Integer ttlInMinutes);

  void updateEntry(PmcProjectDto pmcProject, boolean syncTasks);

  void updateEntry(PmcProjectDto pmcProject);

  void collectUpdates(@Observes(during = TransactionPhase.BEFORE_COMPLETION) ElasticUpdate event);

  void performUpdates(@Observes(during = TransactionPhase.AFTER_SUCCESS) ElasticBulkUpdate event);

  void updateTask(PmcTaskDto task);

  void refreshTaskIndex();

  void refreshProcessIndex();

  void deleteTask(String id);

  Map<String, String> getProcessVariablesForProcess(String pid);



  // updateRefObject (evtl. updateRefObjectForProject)
  //updateProject
  // findAllProjectsOfType(type)
  // findAllActiveProjects

}
