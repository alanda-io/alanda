package io.alanda.base.service.checklist.statusresolver;

import io.alanda.base.entity.checklist.CheckListItemBackend;
import io.alanda.base.entity.checklist.CheckListItemDefinition;
import io.alanda.base.service.checklist.dto.CheckListItemDefinitionDto;
import io.alanda.base.service.checklist.dto.CheckListItemDto;

public class TaskListItemDbService extends TaskListItemService<Long> {
    public TaskListItemDbService() {
        super(CheckListItemBackend.DB, Long.class);
    }

    @Override
    public Long createTaskListItem(CheckListItemDefinition itemDefinition) {
        return null;
    }

    @Override
    public Long removeTaskListItem(CheckListItemDefinition itemDefinition) {
        return null;
    }

    @Override
    public Boolean getTaskListItemStatus(Long aLong) {
        return null;
    }

    @Override
    public void setTaskListItemStatus(Long aLong, Boolean status) {

    }

    @Override
    public CheckListItemDto getTaskListItem(CheckListItemDefinitionDto definitionDto) {
        return null;
    }
}
