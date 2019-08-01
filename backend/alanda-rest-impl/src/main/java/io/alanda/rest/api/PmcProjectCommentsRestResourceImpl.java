/**
 * 
 */
package io.alanda.rest.api;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

/**
 * @author jlo
 */
public class PmcProjectCommentsRestResourceImpl extends AbstractProjectRestResource<PmcProjectCommentsRestResourceImpl>
  implements
    PmcProjectCommentsRestResource {

  @Inject
  PmcCommentDelegate delegate;

  public PmcProjectCommentsRestResourceImpl() {
    super();
  }

  @Override
  public Response saveComment(RestCommentVo comment) {
    checkPermissionsForProject("read");
    RestCommentVo result = delegate.saveComment(projectRestResource.getPmcProjectGuid(), comment);
    return Response.accepted(result).build();
  }

  @Override
  public List<RestCommentVo> list() {
    checkPermissionsForProject("read");
    return delegate.list(projectRestResource.getPmcProjectGuid());
  }

}
