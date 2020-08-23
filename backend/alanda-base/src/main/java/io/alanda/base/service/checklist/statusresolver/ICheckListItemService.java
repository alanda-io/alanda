package io.alanda.base.service.checklist.statusresolver;

import io.alanda.base.entity.checklist.CheckListItemBackend;
import io.alanda.base.entity.checklist.CheckListItemDefinition;
import io.alanda.base.service.checklist.dto.CheckListItemDto;

import java.util.List;

public interface ICheckListItemService<ID> {

    CheckListItemBackend getTaskBackend();

    CheckListItemDefinition createCheckListItem(CheckListItemDefinition itemDefinition);

    ID removeCheckListItem(CheckListItemDefinition itemDefinition);

    Boolean getCheckListItemStatus(ID id);

    void setCheckListItemStatus(ID id, Boolean status);

    CheckListItemDto getCheckListItem(Long definitionGuid);

    List<CheckListItemDto> getCheckListItems(Iterable<Long> definitionGuids);
}
