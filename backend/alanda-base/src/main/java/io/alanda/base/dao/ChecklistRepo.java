package io.alanda.base.dao;

import io.alanda.base.entity.checklist.CheckList;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.cdi.Eager;

@Eager
public interface ChecklistRepo extends PagingAndSortingRepository<CheckList, Long> {
    Iterable<CheckList> findCheckListsByUserTaskInstance(String userTaskInstance);
}
