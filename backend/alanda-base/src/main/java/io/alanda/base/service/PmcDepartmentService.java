package io.alanda.base.service;

import java.util.List;

import io.alanda.base.dto.PmcDepartmentDto;

public interface PmcDepartmentService {
  
  public static final Long DEPARTMENT_RAN_ID = 1L;
  public static final Long DEPARTMENT_IT_ID = 2L;
  
  public static final String DEPARTMENT_RAN = "RAN";
  public static final String DEPARTMENT_IT = "IT";

  PmcDepartmentDto getPmcDepartment(Long guid);

  PmcDepartmentDto getPmcDepartment(String idName);

  List<PmcDepartmentDto> getDepartmentList();

}
