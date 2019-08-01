/**
 * 
 */
package io.alanda.base.dto;

import java.util.Collection;
import java.util.Date;

/**
 * @author Jens Kornacker
 */
public class PmcBlogDto {

  private Long guid;

  private String title;

  private String content;

  private String teaser;

  private Date createDate;

  private Collection<PmcTagDto> tags;

  private String status;

  private Long blog_id;

  private String authorFirstName;

  private String authorSurname;

  //private String tags;

  /**
   * 
   */
  public PmcBlogDto() {
    // TODO Auto-generated constructor stub
  }

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public String getTeaser() {
    return teaser;
  }

  public void setTeaser(String teaser) {
    this.teaser = teaser;
  }

  public Collection<PmcTagDto> getTags() {
    return tags;
  }

  public void setTags(Collection<PmcTagDto> tags) {
    this.tags = tags;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getBlog_id() {
    return blog_id;
  }

  public void setBlog_id(Long blog_id) {
    this.blog_id = blog_id;
  }

  public String getAuthorFirstName() {
    return authorFirstName;
  }

  public void setAuthorFirstName(String authorFirstName) {
    this.authorFirstName = authorFirstName;
  }

  public String getAuthorSurname() {
    return authorSurname;
  }

  public void setAuthorSurname(String authorSurname) {
    this.authorSurname = authorSurname;
  }

  //  public String getTags() {
  //    return tags;
  //  }
  //
  //  public void setTags(String tags) {
  //    this.tags = tags;
  //  }

}
