package io.alanda.base.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import io.alanda.base.entity.PmcGroup;

@Eager
public interface PmcGroupRepo extends PagingAndSortingRepository<PmcGroup, Long>, QueryByExampleExecutor<PmcGroup> {

  PmcGroup findByGroupName(String groupName);
  
  @Query(nativeQuery = true)
  List<PmcGroup> findByRolesGuid(@Param("roleId") Long roleId);
  
  @Query(nativeQuery = true)
  List<PmcGroup> findByRolesName(@Param("roleName") String roleName);
  
}
