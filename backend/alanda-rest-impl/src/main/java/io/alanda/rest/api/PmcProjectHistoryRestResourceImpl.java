/**
 * 
 */
package io.alanda.rest.api;

import java.util.List;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import io.alanda.base.dto.PmcHistoryLogDto;
import io.alanda.base.entity.PmcHistoryLog.Action;

/**
 * @author jlo
 */
public class PmcProjectHistoryRestResourceImpl extends AbstractProjectRestResource<PmcProjectHistoryRestResourceImpl>
  implements
    PmcProjectHistoryRestResource {

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(PmcProjectHistoryRestResourceImpl.class);

  public PmcProjectHistoryRestResourceImpl(PmcProjectRestResourceImpl projectRestResource) {
    super(projectRestResource);
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.rest.api.PmcProjectHistoryRestResource#saveHistoryLog(com.bpmasters.pmc.rest.api.RestHistoryVo)
   */
  @Override
  public Response saveHistoryLog(PmcHistoryLogDto history) {
    checkPermissionsForProject("read");
    checkPermissionsForLog("history.externalState", "write");
    PmcHistoryLogDto existing = null;
    if (history.getFieldId() != null) {
      PmcHistoryLogDto searchDto = new PmcHistoryLogDto();
      searchDto.setPmcProjectGuid(projectRestResource.getPmcProjectGuid());
      searchDto.setType("externalState");
      searchDto.setFieldId(history.getFieldId());
      Page<PmcHistoryLogDto> page = projectRestResource.getRestApi().getPmcHistoryService().searchHistory(searchDto, 1, 5);
      if (page.getTotalElements() >= 1) {
        existing = page.getContent().get(0);
      }
      //logger.info("found " + page.getTotalElements() + "results -> for search dto: " + searchDto + ", results are: " + page.getContent());

    }
    PmcHistoryLogDto dto = null;
    if (existing != null) {
      dto = existing.withLogDate(history.getLogDate()).withChange(Action.LOG, history.getText());
    } else {
      dto = PmcHistoryLogDto
        .createForExternalState(this.projectRestResource.getPmcProject(), history.getFieldId())
        .withLogDate(history.getLogDate())
        .withChange(Action.LOG, history.getText());
    }
    projectRestResource.getRestApi().getPmcHistoryService().createHistory(dto);

    return Response.accepted().build();
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.rest.api.PmcProjectHistoryRestResource#list()
   */
  @Override
  public List<PmcHistoryLogDto> list() {
    checkPermissionsForProject("read");
    PmcHistoryLogDto searchDto = new PmcHistoryLogDto();
    searchDto.setPmcProjectGuid(projectRestResource.getPmcProjectGuid());
    //    searchDto.setType("externalState");
    //    searchDto.setFieldId(history.getFieldId());
    Page<PmcHistoryLogDto> page = projectRestResource.getRestApi().getPmcHistoryService().searchHistory(searchDto, 1, 2000);
    return page.getContent();
  }

  private void checkPermissionsForLog(String type, String permissions) {

    Subject subject = SecurityUtils.getSubject();
    String key = projectRestResource.getRestApi().getPmcShiroAuthorizationService().getAuthKeyForProject(
      projectRestResource.getPmcProjectGuid(),

      permissions);
    if (type != null)
      key += ":" + type;
    if ( !subject.isPermitted(key)) {
      throw new ForbiddenException("You are not allowed to access the Milestone!");
    }
    return;
  }

}
