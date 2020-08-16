package io.alanda.base.dao;

import io.alanda.base.entity.checklist.CheckListItem;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.cdi.Eager;

@Eager
public interface ChecklistItemRepo extends PagingAndSortingRepository<CheckListItem, Long> {
    Iterable<CheckListItem> findCheckListItemsByDefinitionGuids(Iterable<Long> itemDefinitionGuid);

    CheckListItem findCheckListItemByDefinitionGuid(Long itemDefinitionGuid);
}
