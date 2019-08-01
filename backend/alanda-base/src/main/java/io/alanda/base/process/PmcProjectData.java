package io.alanda.base.process;

import javax.inject.Inject;
import javax.inject.Named;

import org.camunda.bpm.engine.cdi.BusinessProcess;

import io.alanda.base.dao.PmcProjectDao;
import io.alanda.base.entity.PmcProject;

@Named("pmcProjectData")
public class PmcProjectData extends AbstractData {

  @Inject
  protected PmcProjectDao pmcProjectDao;

  @Inject
  public PmcProjectData(BusinessProcess businessProcess) {
    super(businessProcess);
  }

  public PmcProject getPmcProject() {
    return pmcProjectDao.getById(getPmcProjectGuid());
  }

}
