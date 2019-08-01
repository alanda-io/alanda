package io.alanda.base.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import io.alanda.base.entity.PmcHistoryLog;

@Eager
public interface PmcHistoryLogRepo
    extends PagingAndSortingRepository<PmcHistoryLog, Long>, QueryByExampleExecutor<PmcHistoryLog> {

}
