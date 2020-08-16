package io.alanda.base.service.checklist.statusresolver;

import io.alanda.base.entity.checklist.CheckListItemBackend;
import io.alanda.base.entity.checklist.CheckListItemDefinition;
import io.alanda.base.service.checklist.dto.CheckListItemDto;

import java.util.List;

public abstract class CheckListItemService<ID> {
    private final Class<ID> idClass;
    private final CheckListItemBackend taskBackend;

    CheckListItemService(CheckListItemBackend taskBackend, Class<ID> idClass) {
        this.taskBackend = taskBackend;
        this.idClass = idClass;
    }

    public CheckListItemBackend getTaskBackend() {
        return taskBackend;
    }

    public abstract ID createCheckListItem(CheckListItemDefinition itemDefinition);

    public abstract ID removeCheckListItem(CheckListItemDefinition itemDefinition);

    public abstract Boolean getCheckListItemStatus(ID id);

    public abstract void setCheckListItemStatus(ID id, Boolean status);

    public abstract CheckListItemDto getCheckListItem(Long definitionGuid);

    public abstract List<CheckListItemDto> getCheckListItems(Iterable<Long> definitionGuids);

    protected Class<ID> getIdClass() {
        return idClass;
    }

    boolean isValidIdClass(Class<?> idClass) {
        return getIdClass() == idClass;
    }
}
