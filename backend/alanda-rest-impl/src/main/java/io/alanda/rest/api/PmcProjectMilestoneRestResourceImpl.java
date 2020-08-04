/**
 * 
 */
package io.alanda.rest.api;

import javax.ws.rs.ForbiddenException;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcProjectMilestoneDto;
import io.alanda.base.dto.SimpleMilestoneDto;

/**
 * @author jlo
 */
public class PmcProjectMilestoneRestResourceImpl extends AbstractProjectRestResource<PmcProjectHistoryRestResourceImpl>
  implements
    PmcProjectMilestoneRestResource {

  @SuppressWarnings("unused")
  private static final Logger log = LoggerFactory.getLogger(PmcProjectMilestoneRestResourceImpl.class);

  private String milestoneIdName;

  public PmcProjectMilestoneRestResourceImpl(PmcProjectRestResourceImpl projectRestResource, String milestoneIdName) {
    super(projectRestResource);
    this.milestoneIdName = milestoneIdName;
  }

  @Override
  public SimpleMilestoneDto act(RestMilestoneVo msData) {
    checkPermissionsForMilestone("act", "write");
    PmcProjectMilestoneDto ms = projectRestResource.getRestApi().getPmcProjectService().updateProjectMilestone(
      projectRestResource.getProjectId(),
      milestoneIdName,
      null,
      msData.date,
      msData.reason);
    return new SimpleMilestoneDto(ms.getMilestone().getIdName(), ms.getFc(), ms.getBaseline(), ms.getAct());
  }

  @Override
  public SimpleMilestoneDto fc(RestMilestoneVo msData) {
    checkPermissionsForMilestone("fc", "write");
    PmcProjectMilestoneDto ms = projectRestResource.getRestApi().getPmcProjectService().updateProjectMilestone(
      projectRestResource.getProjectId(),
      milestoneIdName,
      msData.date,
      null,
      msData.reason);
    return new SimpleMilestoneDto(ms.getMilestone().getIdName(), ms.getFc(), ms.getBaseline(), ms.getAct());
  }

  @Override
  public SimpleMilestoneDto getMilestone() {
    checkPermissionsForMilestone(null, "read");
    PmcProjectMilestoneDto ms = projectRestResource
      .getRestApi()
      .getPmcProjectService()
      .getProjectMilestoneByProjectAndMsIdName(projectRestResource.getProjectId(), milestoneIdName);
    if (ms == null) {
      return new SimpleMilestoneDto(milestoneIdName, null, null, null);
    }
    return new SimpleMilestoneDto(ms.getMilestone().getIdName(), ms.getFc(), ms.getBaseline(), ms.getAct());
  }

  private void checkPermissionsForMilestone(String type, String permissions) {
    String postfix = null;
    if (type != null)
      postfix = ":" + type;
    Subject subject = SecurityUtils.getSubject();
    String key = projectRestResource.getRestApi().getPmcShiroAuthorizationService().getAuthKeyForMilestone(
      projectRestResource.getPmcProjectGuid(),
      milestoneIdName,
      permissions);
    if (postfix != null)
      key += postfix;
    if ( !subject.isPermitted(key)) {
      throw new ForbiddenException("You are not allowed to access the Milestone!");
    }
    return;
  }

}
