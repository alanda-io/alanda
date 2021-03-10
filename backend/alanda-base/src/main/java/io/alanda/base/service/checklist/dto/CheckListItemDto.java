package io.alanda.base.service.checklist.dto;

import io.alanda.base.entity.checklist.CheckListItemBackend;

public class CheckListItemDto {
    private Object id;
    private CheckListItemDefinitionDto itemDefinition;
    private Boolean status;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public CheckListItemDefinitionDto getItemDefinition() {
        return itemDefinition;
    }

    public void setItemDefinition(CheckListItemDefinitionDto itemDefinition) {
        this.itemDefinition = itemDefinition;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
