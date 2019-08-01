/**
 * 
 */
package io.alanda.rest.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.camunda.bpm.engine.HistoryService;

import io.alanda.base.dto.PmcCommentDto;
import io.alanda.base.dto.PmcProjectProcessDto;
import io.alanda.base.service.PmcCommentService;
import io.alanda.base.service.PmcProcessService;
import io.alanda.base.type.ProcessVariables;

/**
 * @author jlo
 */
@Named
@Alternative
@Priority(0)
public class PmcCommentDelegateImpl implements PmcCommentDelegate {

  @Inject
  PmcCommentService pmcCommentService;

  @Inject
  PmcProcessService pmcProcessService;

  @Inject
  HistoryService historyService;

  @Override
  public RestCommentVo saveComment(long pmcProjectGuid, RestCommentVo comment) {
    PmcProjectProcessDto process = pmcProcessService.getMainProjectProcess(pmcProjectGuid);
    PmcCommentDto dto = new PmcCommentDto();
    dto.setSubject(comment.getSubject());
    dto.setText(comment.getText());
    dto.setProcInstId(process.getProcessInstanceId());
    String commentKey = (String) historyService
      .createHistoricVariableInstanceQuery()
      .processInstanceId(dto.getProcInstId())
      .variableName(ProcessVariables.COMMENT_KEY)
      .singleResult()
      .getValue();
    if (commentKey == null) {
      throw new WebApplicationException(
        Response.serverError().entity("No CommentKey found for ProcInstId: " + dto.getProcInstId()).build());
    }
    dto.setProcessName(process.getLabel());
    dto.setCommentKey(commentKey);
    dto.setPmcProjectGuid(pmcProjectGuid);
    dto = pmcCommentService.insert(dto);
    return map(dto);
  }

  private List<RestCommentVo> map(List<PmcCommentDto> comments) {
    if (comments == null)
      return null;
    return comments.stream().map((in) -> map(in)).collect(Collectors.toList());
  }

  private RestCommentVo map(PmcCommentDto dto) {
    RestCommentVo vo = new RestCommentVo();
    vo.setSubject(dto.getSubject());
    vo.setGuid(dto.getGuid());
    vo.setUser(dto.getAuthorName());
    vo.setText(dto.getText());
    vo.setCommentDate(dto.getUpdateDate());
    vo.setReplies(map(dto.getReplies()));
    return vo;
  }

  @Override
  public List<RestCommentVo> list(long pmcProjectGuid) {
    PmcProjectProcessDto process = pmcProcessService.getMainProjectProcess(pmcProjectGuid);
    List<PmcCommentDto> comments = pmcCommentService.getAllForProcessInstanceId(process.getProcessInstanceId());
    return map(comments);
  }

}
