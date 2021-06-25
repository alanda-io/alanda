package io.alanda.base.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import io.alanda.base.entity.PmcGroup;

@Eager
public interface PmcGroupRepo extends PagingAndSortingRepository<PmcGroup, Long>, QueryByExampleExecutor<PmcGroup> {

  PmcGroup findByGroupName(String groupName);

  List<PmcGroup> findByRoles_Guid(Long roleId);

  List<PmcGroup> findByRoles_Name(String roleName);
  
}
