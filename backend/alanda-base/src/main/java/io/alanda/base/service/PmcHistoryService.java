package io.alanda.base.service;

import org.springframework.data.domain.Page;

import io.alanda.base.dto.PmcHistoryLogDto;

public interface PmcHistoryService {

  void createHistory(PmcHistoryLogDto history);

  Page<PmcHistoryLogDto> listHistory(int pageNumber, int pageSize);

  Page<PmcHistoryLogDto> searchHistory(PmcHistoryLogDto searchDto, int pageNumber, int pageSize);
}
