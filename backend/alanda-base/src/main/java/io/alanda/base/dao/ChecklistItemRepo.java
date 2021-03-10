package io.alanda.base.dao;

import io.alanda.base.entity.checklist.CheckListItem;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.cdi.Eager;

import java.util.List;

@Eager
public interface ChecklistItemRepo extends PagingAndSortingRepository<CheckListItem, Long> {

    List<CheckListItem> findByDefinitionGuidIn(Iterable<Long> itemDefinitionGuid);

    CheckListItem findByDefinitionGuid(Long itemDefinitionGuid);
}
