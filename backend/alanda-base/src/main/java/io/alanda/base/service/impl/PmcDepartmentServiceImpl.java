package io.alanda.base.service.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dao.PmcDepartmentDao;
import io.alanda.base.dto.PmcDepartmentDto;
import io.alanda.base.service.PmcDepartmentService;
import io.alanda.base.util.DozerMapper;

@Stateless
public class PmcDepartmentServiceImpl implements PmcDepartmentService {

  protected static final Logger logger = LoggerFactory.getLogger(PmcDepartmentServiceImpl.class);

  @Inject
  private PmcDepartmentDao pmcDepartmentDao;

  @Inject
  private DozerMapper dozerMapper;

  @Override public PmcDepartmentDto getPmcDepartment(Long guid) {
    return dozerMapper.map(pmcDepartmentDao.getById(guid), PmcDepartmentDto.class);
  }

  @Override public PmcDepartmentDto getPmcDepartment(String idName) {
    return dozerMapper.map(pmcDepartmentDao.getByIdName(idName), PmcDepartmentDto.class);
  }

  @Override
  public List<PmcDepartmentDto> getDepartmentList() {
    return dozerMapper.mapCollection(pmcDepartmentDao.getAll(), PmcDepartmentDto.class);
  }
}
