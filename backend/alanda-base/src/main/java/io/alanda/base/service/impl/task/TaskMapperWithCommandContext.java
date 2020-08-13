package io.alanda.base.service.impl.task;

import io.alanda.base.dto.PmcGroupDto;
import io.alanda.base.type.ProcessVariables;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.IdentityLinkEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.IdentityLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
class TaskMapperWithCommandContext extends TaskMapper {
    private static final Logger log = LoggerFactory.getLogger(TaskMapperWithCommandContext.class);

    @Override
    protected TaskInfo getTaskInfo(TaskEntity task) {
        final TaskInfo taskInfo = new TaskInfo();

        taskInfo.setRefObjectId((Long) task.getExecution().getVariable("refObjectId"));
        taskInfo.setRefObjectType((String) task.getExecution().getVariable("refObjectType"));
        taskInfo.setProcess(task.getExecution().getProcessInstance());
        taskInfo.setProcessDefinition(task.getProcessDefinition());
        taskInfo.setComment((String) task.getExecution().getVariable(ProcessVariables.COMMENT));
        taskInfo.setProcessPackageKey((String) task.getExecution().getVariable(ProcessVariables.PROCESS_PACKAGE_KEY));
        if (taskInfo.getProcessPackageKey() != null) {
            try {
                if (taskInfo.getProcessPackageKey().equals(task.getProcessInstanceId())) {
                    taskInfo.setSuspensionState((Boolean) task.getExecution().getVariable(ProcessVariables.SUSPENSION_STATE));
                } else {
                    final ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(taskInfo.getProcessPackageKey()).singleResult();
                    if (pi != null) {
                        taskInfo.setSuspensionState((Boolean) runtimeService.getVariable(taskInfo.getProcessPackageKey(), ProcessVariables.SUSPENSION_STATE));
                    }
                }
            } catch (Exception ex) {
                log.warn("Task #{} - suspensionState for processPackageKey {} not found.", task.getId(), taskInfo.getProcessPackageKey(), ex);
            }
        }

        // add candidate groups
        final List<IdentityLinkEntity> identityLinksForTask = task.getIdentityLinks();
        for (IdentityLink link : identityLinksForTask) {
            if (link.getType().equals("candidate") && !StringUtils.isEmpty(link.getGroupId())) {
                final PmcGroupDto group = pmcUserService.getGroupById(Long.valueOf(link.getGroupId().trim()));
                if (group != null) {
                    taskInfo.getCandidateGroupNames().add(group.getLongName());
                    taskInfo.getCandidateGroupIds().add(group.getGuid());
                } else {
                    log.warn("Task #{} - candidate Group {} not found.", task.getId(), link.getGroupId());
                }
            }
        }

        return taskInfo;
    }

    @Override
    protected void checkCommandContext() {
        if (Context.getCommandContext() == null) {
            throw new IllegalStateException("Command Context must be set - use " + TaskMapperWithoutCommandContext.class.getName() + " instead");
        }
    }
}
