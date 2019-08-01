package io.alanda.rest.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import io.alanda.base.dto.PmcHistoryLogDto;
import io.alanda.base.service.PmcHistoryService;

import io.alanda.rest.PmcHistoryLogRestService;

public class PmcHistoryLogRestServiceImpl implements PmcHistoryLogRestService {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Inject
  private PmcHistoryService pmcHistoryService;

  public Map<String, Object> search(int pageNumber, int pageSize, PmcHistoryLogDto searchDto) {

    Page<PmcHistoryLogDto> searchResult = pmcHistoryService.searchHistory(searchDto, pageNumber, pageSize);

    Map<String, Object> response = new HashMap<>();
    response.put("results", searchResult.getContent());
    response.put("total", searchResult.getTotalElements());
    return response;
  }
}
