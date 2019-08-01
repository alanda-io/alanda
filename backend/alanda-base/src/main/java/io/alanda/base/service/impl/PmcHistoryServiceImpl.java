package io.alanda.base.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import io.alanda.base.dao.PmcHistoryLogRepo;
import io.alanda.base.dto.PmcHistoryLogDto;
import io.alanda.base.entity.PmcHistoryLog;
import io.alanda.base.service.PmcHistoryService;
import io.alanda.base.util.DozerMapper;

@Singleton
@ApplicationScoped
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class PmcHistoryServiceImpl implements PmcHistoryService {

  @Inject
  private PmcHistoryLogRepo histLogRepo;

  @Inject
  private DozerMapper dozerMapper;

  @Override
  public void createHistory(PmcHistoryLogDto history) {
    histLogRepo.save(dozerMapper.map(history, PmcHistoryLog.class));
  }

  @Override
  public Page<PmcHistoryLogDto> listHistory(int pageNumber, int pageSize) {
    PageRequest pr = new PageRequest(pageNumber, pageSize);

    Page<PmcHistoryLog> searchResult = histLogRepo.findAll(pr);
    List<PmcHistoryLogDto> res = new ArrayList<>();
    for (PmcHistoryLog pmcHist : searchResult) {
      PmcHistoryLogDto tmp = dozerMapper.map(pmcHist, PmcHistoryLogDto.class);
      res.add(tmp);
    }
    Page<PmcHistoryLogDto> retVal = new PageImpl<>(res, pr, searchResult.getNumberOfElements());
    return retVal;
  }

  @Override
  public Page<PmcHistoryLogDto> searchHistory(PmcHistoryLogDto searchDto, int pageNumber, int pageSize) {
    PageRequest pr = new PageRequest(pageNumber - 1, pageSize, Sort.Direction.DESC, "logDate");
    ExampleMatcher matcher = ExampleMatcher
      .matching()
      .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
      .withIgnoreCase()
      .withIgnoreNullValues();
    Example<PmcHistoryLog> exp = Example.of(dozerMapper.map(searchDto, PmcHistoryLog.class), matcher);
    Page<PmcHistoryLog> searchResult = histLogRepo.findAll(exp, pr);
    List<PmcHistoryLogDto> res = new ArrayList<>();
    for (PmcHistoryLog pmcHist : searchResult) {
      PmcHistoryLogDto tmp = dozerMapper.map(pmcHist, PmcHistoryLogDto.class);
      res.add(tmp);
    }
    Page<PmcHistoryLogDto> retVal = new PageImpl<>(res, pr, searchResult.getNumberOfElements());
    return retVal;
  }

}
