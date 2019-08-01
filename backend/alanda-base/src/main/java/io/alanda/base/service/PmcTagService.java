package io.alanda.base.service;

import java.util.List;

import io.alanda.base.dto.PmcTagDto;

public interface PmcTagService {

  public List<PmcTagDto> getTagList();

  public List<PmcTagDto> getTagList(String query);

}
