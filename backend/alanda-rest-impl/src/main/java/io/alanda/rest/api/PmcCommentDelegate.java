/**
 * 
 */
package io.alanda.rest.api;

import java.util.List;

/**
 * @author jlo
 */
public interface PmcCommentDelegate {

  public RestCommentVo saveComment(long pmcProjectGuid, RestCommentVo comment);

  public List<RestCommentVo> list(long pmcProjectGuid);

}
