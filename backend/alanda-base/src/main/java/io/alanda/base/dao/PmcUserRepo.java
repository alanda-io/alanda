package io.alanda.base.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import io.alanda.base.entity.PmcUser;

@Eager
public interface PmcUserRepo extends PagingAndSortingRepository<PmcUser, Long>, QueryByExampleExecutor<PmcUser> {

  List<PmcUser> getByGroupList_Guid(Long guid, Sort sort);

  PmcUser getByEmail(String email);
}
