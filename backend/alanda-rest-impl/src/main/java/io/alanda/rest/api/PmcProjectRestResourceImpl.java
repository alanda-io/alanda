/**
 * 
 */
package io.alanda.rest.api;

import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.ForbiddenException;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.entity.PmcProject;

/**
 * @author jlo
 */
public class PmcProjectRestResourceImpl implements PmcProjectRestResource {

  private String projectId;

  private PmcRestApiImpl restApi;

  public PmcRestApiImpl getRestApi() {
    return restApi;
  }

  public PmcProjectRestResourceImpl(String projectId, PmcRestApiImpl restApi) {
    super();
    this.projectId = projectId;
    this.restApi = restApi;
  }

  public String getProjectId() {
    return projectId;
  }

  @Override
  public PmcProjectMilestoneRestResource getPmcProjectMilestoneRestResource(String milestoneIdName) {
    return new PmcProjectMilestoneRestResourceImpl(this, milestoneIdName);
  }

  public Long getPmcProjectGuid() {
    return this.getRestApi().getPmcProjectDao().getByProjectId(projectId).getGuid();
  }

  public PmcProject getPmcProject() {
    return this.getRestApi().getPmcProjectDao().getByProjectId(projectId);
  }

  @Override
  public PmcProjectHistoryRestResource getPmcProjectHistoryRestResource() {
    return new PmcProjectHistoryRestResourceImpl(this);
  }

  public void checkPermissionsForProject(String permissions) {
    Subject subject = SecurityUtils.getSubject();
    String key = getRestApi().getPmcShiroAuthorizationService().getAuthKeyForProject(
      getPmcProjectGuid(),

      permissions);

    if ( !subject.isPermitted(key)) {
      throw new ForbiddenException("You are not allowed to access the Project!");
    }
    return;

  }

  @Override
  public PmcProjectCommentsRestResource getPmcProjectCommentsRestResource() {
    return CDI.current().select(PmcProjectCommentsRestResourceImpl.class).get().forProject(this);
  }

  @Override
  public RestProjectVo getRestProject() {
    checkPermissionsForProject("read");
    PmcProject p = getPmcProject();
    RestProjectVo rpv = new RestProjectVo(p);
    PmcUserDto user = this.getRestApi().getUserCache().get(p.getOwnerId());
    if (user != null) {
      rpv.setOwnerName(user.getFirstName() + " " + user.getSurname());
    }
    return rpv;
  }

}
