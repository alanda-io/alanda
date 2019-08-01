package io.alanda.base.service.impl;

import java.util.List;

import javax.inject.Inject;

import io.alanda.base.dao.PmcTagDao;
import io.alanda.base.dto.PmcTagDto;
import io.alanda.base.service.PmcTagService;
import io.alanda.base.util.DozerMapper;

/**
 * @author Jens Kornacker
 */

public class PmcTagServiceImpl implements PmcTagService {

  @Inject
  private PmcTagDao pmcTagDao;

  @Inject
  private DozerMapper dozerMapper;

  @Override
  public List<PmcTagDto> getTagList() {
    // TODO Auto-generated method stub
    return dozerMapper.mapCollection(pmcTagDao.getAll(), PmcTagDto.class);
  }

  @Override
  public List<PmcTagDto> getTagList(String query) {
    // TODO Auto-generated method stub
    return dozerMapper.mapCollection(pmcTagDao.getTagList(query), PmcTagDto.class);
  }

}
