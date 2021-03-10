package io.alanda.base.dao;

import io.alanda.base.entity.checklist.CheckList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.cdi.Eager;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.NamedQuery;
import java.util.List;


@Eager
public interface ChecklistRepo extends CrudRepository<CheckList, Long> {

    List<CheckList> findByUserTaskInstance(String userTaskInstance);
}
