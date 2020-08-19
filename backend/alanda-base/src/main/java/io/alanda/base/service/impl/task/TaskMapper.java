package io.alanda.base.service.impl.task;

import io.alanda.base.dto.PmcTaskDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.service.PmcUserService;
import io.alanda.base.type.PmcProjectState;
import io.alanda.base.type.ProcessVariables;
import io.alanda.base.util.cache.UserCache;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

abstract class TaskMapper {
    private static final Logger log = LoggerFactory.getLogger(TaskMapper.class);

    protected static final FastDateFormat TASK_LIST_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

    @Inject
    protected UserCache userCache;

    @Inject
    protected PmcUserService pmcUserService;

    @Inject
    protected RuntimeService runtimeService;

    @Inject
    protected TaskService taskService;

    /**
     * Maps a {@link TaskEntity} to a {@link PmcTaskDto}, either using information from an active {@link org.camunda.bpm.engine.impl.interceptor.CommandContext}
     * or via other sources.
     *
     * @param task The {@link TaskEntity} to map
     * @return the mapped {@link PmcTaskDto}
     */
    public PmcTaskDto mapTask(TaskEntity task) {
        checkCommandContext();

        final TaskInfo taskInfo = getTaskInfo(task);
        String formKey = taskService.createTaskQuery().taskId(task.getId()).initializeFormKeys().singleResult().getFormKey();
        PmcTaskDto dto = new PmcTaskDto();
        dto.setState(PmcProjectState.ACTIVE);
        dto.setTaskId(task.getId());
        dto.setTaskType(taskInfo.getRefObjectType());
        dto.setTaskName(task.getName());
        dto.setObjectName(taskInfo.getProcess().getBusinessKey());
        dto.setFormKey(formKey);
        dto.setAssigneeId(task.getAssignee());
        String assigneeId = task.getAssignee();
        if (!StringUtils.isEmpty(assigneeId)) {
            try {
                PmcUserDto user = userCache.get(Long.valueOf(assigneeId));
                dto.setAssignee(user.getFirstName() + " " + user.getSurname());
            } catch (Exception ex) {
                log.warn("Task # {}({}) cant load assignee {}", task.getId(), task.getName(), assigneeId, ex);
            }
        }
        dto.setAssigneeId(assigneeId);
        Date created = task.getCreateTime();
        if (created != null) {
            dto.setCreated(TASK_LIST_DATE_FORMAT.format(created));
        }
        Date due = task.getDueDate();
        if (due != null) {
            dto.setDue(TASK_LIST_DATE_FORMAT.format(due));
        }
        Date followUp = task.getFollowUpDate();
        if (followUp != null) {
            dto.setFollowUp(TASK_LIST_DATE_FORMAT.format(followUp));
        }
        dto.setProcessDefinitionId(task.getProcessDefinitionId());
        dto.setProcessInstanceId(task.getProcessInstanceId());
        dto.setProcessName(taskInfo.getProcessDefinition().getName());
        dto.setProcessDefinitionKey(taskInfo.getProcessDefinition().getKey());
        if (task.getFollowUpDate() != null) {
            dto.setFollowUp(TASK_LIST_DATE_FORMAT.format(task.getFollowUpDate()));
        }
        dto.setDescription(task.getDescription());
        dto.setExecutionId(task.getExecutionId());
        dto.setProcessPackageKey(taskInfo.getProcessPackageKey());
        dto.setComment(taskInfo.getComment());
        dto.setSuspensionState(taskInfo.getSuspensionState() != null ? taskInfo.getSuspensionState() : false);
        dto.setObjectId(taskInfo.getRefObjectId());
        dto.setCandidateGroups(new ArrayList<>(taskInfo.getCandidateGroupNames()));
        dto.setCandidateGroupIds(new ArrayList<>(taskInfo.getCandidateGroupIds()));
        Long pmcProjectGuid = (Long) runtimeService.getVariable(task.getExecutionId(), ProcessVariables.PMC_PROJECT_GUID);
        if (pmcProjectGuid != null) {
            dto.setPmcProjectGuid(pmcProjectGuid);
        }

        return dto;
    }

    /**
     * Retrieves info for a {@link org.camunda.bpm.engine.task.Task}, either via an active {@link org.camunda.bpm.engine.impl.interceptor.CommandContext}
     * or other methods.
     *
     * @param taskEntity the {@link TaskEntity} to retrieve information for
     * @return a POJO containing task information
     */
    protected abstract TaskInfo getTaskInfo(TaskEntity taskEntity);

    /**
     * Checks if {@code TaskMapper} implementation is running in it's intended context, i.e. if {@link org.camunda.bpm.engine.impl.interceptor.CommandContext}
     * is set or not.
     *
     * This sanity check only fails when {@link TaskRetrieverService#getTask(TaskEntity)} uses the wrong {@code TaskMapper} implementation
     *
     * @throws IllegalStateException if {@code CommandContext} isn't what it's expected to be; should never be caught
     */
    protected abstract void checkCommandContext();

    /**
     * Simple POJO bundling information to be retrieved by a implementation of {@code TaskMapper}
     */
    protected static class TaskInfo {
        private Long refObjectId = null;
        private String refObjectType = null;
        private ProcessInstance process = null;
        private ProcessDefinition processDefinition = null;
        private String processPackageKey = null;
        private String comment = null;
        private Boolean suspensionState = null;
        private Set<String> candidateGroupNames = new HashSet<>();
        private Set<Long> candidateGroupIds = new HashSet<>();

        public Long getRefObjectId() {
            return refObjectId;
        }

        public void setRefObjectId(Long refObjectId) {
            this.refObjectId = refObjectId;
        }

        public String getRefObjectType() {
            return refObjectType;
        }

        public void setRefObjectType(String refObjectType) {
            this.refObjectType = refObjectType;
        }

        public ProcessInstance getProcess() {
            return process;
        }

        public void setProcess(ProcessInstance process) {
            this.process = process;
        }

        public ProcessDefinition getProcessDefinition() {
            return processDefinition;
        }

        public void setProcessDefinition(ProcessDefinition processDefinition) {
            this.processDefinition = processDefinition;
        }

        public String getProcessPackageKey() {
            return processPackageKey;
        }

        public void setProcessPackageKey(String processPackageKey) {
            this.processPackageKey = processPackageKey;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public Boolean getSuspensionState() {
            return suspensionState;
        }

        public void setSuspensionState(Boolean suspensionState) {
            this.suspensionState = suspensionState;
        }

        public Set<String> getCandidateGroupNames() {
            return candidateGroupNames;
        }

        public void setCandidateGroupNames(Set<String> candidateGroupNames) {
            this.candidateGroupNames = candidateGroupNames;
        }

        public Set<Long> getCandidateGroupIds() {
            return candidateGroupIds;
        }

        public void setCandidateGroupIds(Set<Long> candidateGroupIds) {
            this.candidateGroupIds = candidateGroupIds;
        }
    }
}
