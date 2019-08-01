package io.alanda.rest.impl;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcCommentDto;
import io.alanda.base.service.PmcCommentService;
import io.alanda.base.type.ProcessVariables;

import io.alanda.rest.PmcCommentRestService;
import io.alanda.rest.api.PmcCommentRestResult;

public class PmcCommentRestServiceImpl implements PmcCommentRestService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Inject
  private PmcCommentService pmcCommentService;

  @Inject
  private HistoryService historyService;

  @Inject
  private RepositoryService repositoryService;

  @Override
  public PmcCommentRestResult getAllForProcessInstanceId(String processInstanceId) {
    PmcCommentRestResult result = new PmcCommentRestResult();

    List<PmcCommentDto> commentDtos = pmcCommentService.getAllForProcessInstanceId(processInstanceId);
    result.setComments(commentDtos);
    result.setRefObjectIdName(null);
    result.setFilterByRefObject(false);
    return result;
  }

  @Override
  public PmcCommentRestResult getAllForProcessInstanceIdAndRefObjectId(String processInstanceId, Long refObjectId) {
    PmcCommentRestResult result = new PmcCommentRestResult();
    List<PmcCommentDto> commentDtos = pmcCommentService.getAllForProcessInstanceIdAndRefObjectId(processInstanceId, refObjectId);
    result.setComments(commentDtos);
    result.setRefObjectIdName(null);
    result.setFilterByRefObject(false);
    return result;
  }

  @Override
  public Response insertComment(PmcCommentDto pmcCommentDto) {
    if (pmcCommentDto == null) {
      return Response.serverError().entity("No comment posted").build();
    }
    if (pmcCommentDto.getProcInstId() == null) {
      return Response.serverError().entity("No processInstanceId, sorry").build();
    }
    String commentKey = (String) historyService
      .createHistoricVariableInstanceQuery()
      .processInstanceId(pmcCommentDto.getProcInstId())
      .variableName(ProcessVariables.COMMENT_KEY)
      .singleResult()
      .getValue();
    if (commentKey == null)
      return Response.serverError().entity("No CommentKey found for ProcInstId: " + pmcCommentDto.getProcInstId()).build();

    HistoricVariableInstance pmcProject = historyService
      .createHistoricVariableInstanceQuery()
      .processInstanceId(pmcCommentDto.getProcInstId())
      .variableName(ProcessVariables.PMC_PROJECT_GUID)
      .singleResult();
    
    Long pmcProjectGuid = null;
    if (pmcProject != null) pmcProjectGuid = (Long) pmcProject.getValue();
    
    if (pmcCommentDto.getTaskId() != null) {
      HistoricTaskInstance hti = historyService.createHistoricTaskInstanceQuery().taskId(pmcCommentDto.getTaskId()).singleResult();
      pmcCommentDto.setTaskName(hti.getName());
    }
    HistoricProcessInstance hpi = historyService
      .createHistoricProcessInstanceQuery()
      .processInstanceId(pmcCommentDto.getProcInstId())
      .singleResult();
    ProcessDefinition procDef = repositoryService.getProcessDefinition(hpi.getProcessDefinitionId());
    pmcCommentDto.setProcessName(procDef.getName());

    pmcCommentDto.setCommentKey(commentKey);
    pmcCommentDto.setPmcProjectGuid(pmcProjectGuid);

    pmcCommentDto = pmcCommentService.insert(pmcCommentDto);
    return new ResponseBuilderImpl().entity(pmcCommentDto).status(Status.CREATED).build();
  }

}
