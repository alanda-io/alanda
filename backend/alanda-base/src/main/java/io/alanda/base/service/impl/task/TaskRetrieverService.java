package io.alanda.base.service.impl.task;

import io.alanda.base.dto.PmcTaskDto;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Simple service to map a {@link TaskEntity} to a {@link PmcTaskDto}.
 */
@ApplicationScoped
public class TaskRetrieverService {
    @Inject
    private TaskMapperWithoutCommandContext mapperWithoutCommandContext;

    @Inject
    private TaskMapperWithCommandContext mapperWithCommandContext;

    /**
     * Maps a {@link TaskEntity} to a {@link PmcTaskDto}
     * @param task the task to map from
     * @return the optional, mapped task DTO
     */
    public PmcTaskDto getTask(TaskEntity task) {
        return getTaskMapper().mapTask(task);
    }

    /**
     * Returns the correct mapper implementation depending on the existence of an active {@link org.camunda.bpm.engine.impl.interceptor.CommandContext}
     * @return an implementation of a {@link TaskMapper}
     */
    private TaskMapper getTaskMapper() {
        return Context.getCommandContext() != null ? mapperWithCommandContext : mapperWithoutCommandContext;
    }
}
