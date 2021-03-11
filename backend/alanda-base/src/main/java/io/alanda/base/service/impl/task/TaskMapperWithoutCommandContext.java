package io.alanda.base.service.impl.task;

import io.alanda.base.dto.PmcGroupDto;
import io.alanda.base.type.ProcessVariables;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.IdentityLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
class TaskMapperWithoutCommandContext extends TaskMapper {
    private static final Logger log = LoggerFactory.getLogger(TaskMapperWithoutCommandContext.class);

    @Inject
    private TaskService taskService;
    @Inject
    private RepositoryService repositoryService;

    @Override
    protected TaskInfo getTaskInfo(TaskEntity task) {
        final TaskInfo taskInfo = new TaskInfo();

        taskInfo.setRefObjectId((Long) taskService.getVariable(task.getId(), "refObjectId"));
        taskInfo.setRefObjectType((String) taskService.getVariable(task.getId(), "refObjectType"));
        taskInfo.setProcess(runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult());
        taskInfo.setProcessDefinition(repositoryService.getProcessDefinition(task.getProcessDefinitionId()));
        taskInfo.setComment((String) this.runtimeService.getVariable(task.getExecutionId(), ProcessVariables.COMMENT));
        taskInfo.setProcessPackageKey((String) runtimeService.getVariable(task.getProcessInstanceId(), ProcessVariables.PROCESS_PACKAGE_KEY));
        if (taskInfo.getProcessPackageKey() != null) {
            try {
                ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(taskInfo.getProcessPackageKey()).singleResult();
                if (pi != null) {
                    taskInfo.setSuspensionState((Boolean) runtimeService.getVariable(taskInfo.getProcessPackageKey(), ProcessVariables.SUSPENSION_STATE));
                }
            } catch (Exception ex) {
                log.warn("Task #{} - suspensionState for processPackageKey {} not found.", task.getId(), taskInfo.getProcessPackageKey(), ex);
            }
        }

        // add candidate groups
        final List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(task.getId());
        for (IdentityLink link : identityLinksForTask) {
            if (link.getType().equals("candidate") && !StringUtils.isEmpty(link.getGroupId())) {
                try {
                    final PmcGroupDto group = userCache.getGroup(Long.valueOf(link.getGroupId().trim()));

                    if (group != null) {
                        taskInfo.getCandidateGroupNames().add(group.getLongName());
                        taskInfo.getCandidateGroupIds().add(group.getGuid());
                    } else {
                        log.warn("Did not find a group with id {} for task {}", link.getGroupId(), task.getId());
                    }
                } catch (NumberFormatException ex) {
                    log.warn("Task #{} - candidate Group {} no valid group id.", task.getId(), link.getGroupId());
                }
            }
        }

        return taskInfo;
    }

    @Override
    protected void checkCommandContext() {
        if (Context.getCommandContext() != null) {
            throw new IllegalStateException("Command Context must not be set - use " + TaskMapperWithCommandContext.class.getName() + " instead");
        }
    }
}
