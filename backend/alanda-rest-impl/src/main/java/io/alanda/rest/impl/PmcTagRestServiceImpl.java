package io.alanda.rest.impl;

import java.util.List;

import javax.inject.Inject;

import io.alanda.base.dto.PmcTagDto;
import io.alanda.base.service.PmcTagService;

import io.alanda.rest.PmcTagRestService;

public class PmcTagRestServiceImpl implements PmcTagRestService {

  @Inject
  private PmcTagService pmcTagService;

  @Override
  public List<PmcTagDto> getAll() {
    List<PmcTagDto> pmcTagDtos = pmcTagService.getTagList();
    return pmcTagDtos;
  }

  @Override
  public List<PmcTagDto> search(String query) {
    List<PmcTagDto> pmcTagDtos = pmcTagService.getTagList(query);
    return pmcTagDtos;
  }

}
