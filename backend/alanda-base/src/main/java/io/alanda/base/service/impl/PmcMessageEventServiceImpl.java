package io.alanda.base.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import io.alanda.base.service.PmcMessageEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PmcMessageEventServiceImpl implements PmcMessageEventService {

  private static final Logger log = LoggerFactory.getLogger(PmcMessageEventServiceImpl.class);

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private RepositoryService repositoryService;

  @Override
  public void sendMessageToCatchEvent(String messageName, String processDefinitionKey, Long pmcProjectGuid) {
    ProcessDefinition processDefinition = repositoryService
      .createProcessDefinitionQuery()
      .processDefinitionKey(processDefinitionKey)
      .latestVersion()
      .singleResult();

    List<ProcessInstance> pis = runtimeService
      .createProcessInstanceQuery()
      .processDefinitionId(processDefinition.getId())
      .variableValueEquals("pmcProjectGuid", pmcProjectGuid)
      .list();

    for (ProcessInstance pi : pis) {
      List<EventSubscription> evSubList = runtimeService
        .createEventSubscriptionQuery()
        .processInstanceId(pi.getProcessInstanceId())
        .eventName(messageName)
        .list();
      if (evSubList.size() > 1) {
        throw new IllegalStateException("Found " + evSubList.size() + " subscriptions");
      } else if (evSubList.size() == 1) {
        runtimeService.messageEventReceived(messageName, evSubList.get(0).getExecutionId());
        log.info("message to {} successfully sent", processDefinitionKey);
      }
    }
  }

}
