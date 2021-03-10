package io.alanda.base.service.checklist.statusresolver;

import io.alanda.base.dao.ChecklistItemRepo;
import io.alanda.base.entity.checklist.CheckListItem;
import io.alanda.base.entity.checklist.CheckListItemBackend;
import io.alanda.base.entity.checklist.CheckListItemDefinition;
import io.alanda.base.service.checklist.dto.CheckListItemDefinitionDto;
import io.alanda.base.service.checklist.dto.CheckListItemDto;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CheckListItemDbService implements ICheckListItemService<Long> {

    @Inject
    private ChecklistItemRepo checklistItemRepo;

    @Override
    public CheckListItemBackend getTaskBackend() {
        return CheckListItemBackend.DB;
    }

    @Override
    public CheckListItemDefinition createCheckListItem(CheckListItemDefinition itemDefinition) {
        CheckListItem checkListItem = new CheckListItem();
        checkListItem.setStatus(false);
        itemDefinition.addCheckListItem(checkListItem);
        return itemDefinition;
    }

    @Override
    public Long removeCheckListItem(CheckListItemDefinition itemDefinition) {
        return null;
    }

    @Override
    public Boolean getCheckListItemStatus(Long aLong) {
        return null;
    }

    @Override
    public void setCheckListItemStatus(Long aLong, Boolean status) {
        CheckListItem checkListItem = checklistItemRepo.findOne(aLong);
        checkListItem.setStatus(status);
    }

    @Override
    public CheckListItemDto getCheckListItem(Long definitionGuid) {
        return mapToItemDto(checklistItemRepo.findByDefinitionGuid(definitionGuid));
    }

    @Override
    public List<CheckListItemDto> getCheckListItems(Iterable<Long> definitionGuids) {
        return mapToItemDtos(checklistItemRepo.findByDefinitionGuidIn(definitionGuids));
    }

    private CheckListItemDto mapToItemDto(CheckListItem item) {
        final CheckListItemDto itemDto = new CheckListItemDto();
        itemDto.setId(item.getGuid());
        itemDto.setItemDefinition(mapToCheckListItemDefinitionDto(item.getDefinition()));
        itemDto.setStatus(item.getStatus());
        return itemDto;
    }

    private List<CheckListItemDto> mapToItemDtos(List<CheckListItem> items) {
        return items.stream().map(this::mapToItemDto).collect(Collectors.toList());
    }

    private CheckListItemDefinitionDto mapToCheckListItemDefinitionDto(CheckListItemDefinition checkListItemDefinition) {
        CheckListItemDefinitionDto checkListItemDefinitionDto = new CheckListItemDefinitionDto();
        checkListItemDefinitionDto.setSortOrder(checkListItemDefinition.getSortOrder().longValue());
        checkListItemDefinitionDto.setRequired(checkListItemDefinition.getRequired());
        checkListItemDefinitionDto.setKey(checkListItemDefinition.getKey());
        checkListItemDefinitionDto.setDisplayText(checkListItemDefinition.getDisplayText());
        checkListItemDefinitionDto.setCustom(checkListItemDefinition.getCheckListTemplate() != null ? false : true);
        return checkListItemDefinitionDto;
    }
}
