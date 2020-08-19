package io.alanda.base.service.checklist.statusresolver;

import io.alanda.base.dao.ChecklistItemRepo;
import io.alanda.base.entity.checklist.CheckListItem;
import io.alanda.base.entity.checklist.CheckListItemBackend;
import io.alanda.base.entity.checklist.CheckListItemDefinition;
import io.alanda.base.service.checklist.dto.CheckListItemDto;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CheckListItemDbService extends CheckListItemService<Long> {
    @Inject
    private ChecklistItemRepo checklistItemRepo;

    public CheckListItemDbService() {
        super(CheckListItemBackend.DB, Long.class);
    }

    @Override
    public Long createCheckListItem(CheckListItemDefinition itemDefinition) {
        return null;
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

    }

    @Override
    public CheckListItemDto getCheckListItem(Long definitionGuid) {
        return mapToItemDto(checklistItemRepo.findCheckListItemByDefinitionGuid(definitionGuid));
    }

    @Override
    public List<CheckListItemDto> getCheckListItems(Iterable<Long> definitionGuids) {
        return mapToItemDtos(checklistItemRepo.findCheckListItemsByDefinitionGuid(definitionGuids));
    }

    private CheckListItemDto mapToItemDto(CheckListItem item) {
        final CheckListItemDto itemDto = new CheckListItemDto();

        itemDto.setId(item.getGuid());
        // itemDto.getItemDefinition(item.getDefinition()); // TODO map
        itemDto.setStatus(item.getStatus());

        return itemDto;
    }

    private List<CheckListItemDto> mapToItemDtos(Iterable<CheckListItem> items) {
        return StreamSupport.stream(items.spliterator(), false).map(this::mapToItemDto).collect(Collectors.toList());
    }
}
