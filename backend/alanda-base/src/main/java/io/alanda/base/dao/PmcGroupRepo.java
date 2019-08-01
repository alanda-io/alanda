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
  
  public PmcGroup findByGroupName(String groupName);
  
  @Query(nativeQuery = true)
  public List<PmcGroup> findByRole(@Param("roleId") Long roleId);
  
  // PmcGroup.findByRolename
  @Query(nativeQuery = true)
  public List<PmcGroup> findByRolename(@Param("roleName") String roleName);
  
}
