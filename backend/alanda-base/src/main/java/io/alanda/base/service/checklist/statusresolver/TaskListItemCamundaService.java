package io.alanda.base.service.checklist.statusresolver;

import io.alanda.base.entity.checklist.CheckListItemBackend;
import io.alanda.base.entity.checklist.CheckListItemDefinition;
import io.alanda.base.service.checklist.dto.CheckListItemDefinitionDto;
import io.alanda.base.service.checklist.dto.CheckListItemDto;

public class TaskListItemCamundaService extends TaskListItemService<String> {
    public TaskListItemCamundaService(){
        super(CheckListItemBackend.CAMUNDA, String.class);
    }

    @Override
    public String createTaskListItem(CheckListItemDefinition itemDefinition) {
        // start process
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String removeTaskListItem(CheckListItemDefinition itemDefinition) {
        // close/remove process
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Boolean getTaskListItemStatus(String s) {
        // is task open?
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setTaskListItemStatus(String s, Boolean status) {
        // set task open / closed
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public CheckListItemDto getTaskListItem(CheckListItemDefinitionDto definitionDto) {
        // set task open / closed
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
