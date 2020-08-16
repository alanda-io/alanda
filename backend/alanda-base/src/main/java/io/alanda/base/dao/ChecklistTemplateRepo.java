package io.alanda.base.dao;

import io.alanda.base.entity.checklist.CheckListTemplate;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.cdi.Eager;

@Eager
public interface ChecklistTemplateRepo extends PagingAndSortingRepository<CheckListTemplate, Long> {
}
