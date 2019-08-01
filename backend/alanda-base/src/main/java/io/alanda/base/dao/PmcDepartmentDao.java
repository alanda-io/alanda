package io.alanda.base.dao;

import io.alanda.base.entity.PmcDepartment;

public interface PmcDepartmentDao extends CrudDao<PmcDepartment> {

  PmcDepartment getByIdName(String idName);
}
