package io.alanda.base.service.checklist.statusresolver;

import io.alanda.base.entity.checklist.CheckListItemBackend;
import io.alanda.base.entity.checklist.CheckListItemDefinition;
import io.alanda.base.service.checklist.dto.CheckListItemDefinitionDto;
import io.alanda.base.service.checklist.dto.CheckListItemDto;

public abstract class TaskListItemService<ID> {
    private final Class<ID> idClass;
    private final CheckListItemBackend taskBackend;

    TaskListItemService(CheckListItemBackend taskBackend, Class<ID> idClass) {
        this.taskBackend = taskBackend;
        this.idClass = idClass;
    }

    public CheckListItemBackend getTaskBackend() {
        return taskBackend;
    }

    public abstract ID createTaskListItem(CheckListItemDefinition itemDefinition);

    public abstract ID removeTaskListItem(CheckListItemDefinition itemDefinition);

    public abstract Boolean getTaskListItemStatus(ID id);

    public abstract void setTaskListItemStatus(ID id, Boolean status);

    public abstract CheckListItemDto getTaskListItem(CheckListItemDefinitionDto definitionDto);

    protected Class<ID> getIdClass() {
        return idClass;
    }

    boolean isValidIdClass(Class<?> idClass) {
        return getIdClass() == idClass;
    }
}
