/**
 * 
 */
package io.alanda.rest.api;

import java.util.List;

import io.alanda.base.dto.PmcCommentDto;

/**
 * @author jlo
 */
public class PmcCommentRestResult {

  /**
   * response.put("comments", comments); response.put("refObjectIdName", refIdName); response.put("filterByRefObject",
   * filterByRefObject);
   */

  private List<PmcCommentDto> comments;

  private String refObjectIdName;

  private boolean filterByRefObject;

  public List<PmcCommentDto> getComments() {
    return comments;
  }

  public void setComments(List<PmcCommentDto> comments) {
    this.comments = comments;
  }

  public String getRefObjectIdName() {
    return refObjectIdName;
  }

  public void setRefObjectIdName(String refObjectIdName) {
    this.refObjectIdName = refObjectIdName;
  }

  public boolean isFilterByRefObject() {
    return filterByRefObject;
  }

  public void setFilterByRefObject(boolean filterByRefObject) {
    this.filterByRefObject = filterByRefObject;
  }

  @Override
  public String toString() {
    return "PmcCommentRestResult [comments=" +
      comments +
      ", refObjectIdName=" +
      refObjectIdName +
      ", filterByRefObject=" +
      filterByRefObject +
      "]";
  }

}
