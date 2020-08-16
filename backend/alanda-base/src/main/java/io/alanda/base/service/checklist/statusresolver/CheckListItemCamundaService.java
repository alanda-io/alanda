package io.alanda.base.service.checklist.statusresolver;

import io.alanda.base.entity.checklist.CheckListItemBackend;
import io.alanda.base.entity.checklist.CheckListItemDefinition;
import io.alanda.base.service.checklist.dto.CheckListItemDto;

import java.util.List;

public class CheckListItemCamundaService extends CheckListItemService<String> {
    public CheckListItemCamundaService(){
        super(CheckListItemBackend.CAMUNDA, String.class);
    }

    @Override
    public String createCheckListItem(CheckListItemDefinition itemDefinition) {
        // start process
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String removeCheckListItem(CheckListItemDefinition itemDefinition) {
        // close/remove process
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Boolean getCheckListItemStatus(String s) {
        // is task open?
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setCheckListItemStatus(String s, Boolean status) {
        // set task open / closed
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public CheckListItemDto getCheckListItem(Long definitionGuid) {
        // set task open / closed
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<CheckListItemDto> getCheckListItems(Iterable<Long> definitionGuids) {
        return null;
    }
}
