package io.alanda.base.entity;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "PMC_BLOG")
public class PmcBlog extends AbstractAuditEntity {

	@Column(name = "TITLE")
	private String title;
	
  @Column(name = "TEASER")
  private String teaser;

	@Column(name = "CONTENT")
  @Lob()
	private String content;
	
  @Column(name = "STATUS")
  private String status;

  @ManyToMany()
  @JoinTable(name = "PMC_BLOG_TAG", joinColumns = @JoinColumn(name = "BLOG", referencedColumnName = "GUID"), inverseJoinColumns = @JoinColumn(name = "TAG", referencedColumnName = "ID"))
  Collection<PmcTag> tags;

  @Column(name = "PREDECESSOR_ID")
  private Long predecessor_id;

  @Column(name = "PUBLISH_DATE")
  private Date publishDate;

  @Column(name = "BLOG_ID")
  private Long blog_id;

  @Column(name = "AUTHOR_FIRSTNAME")
  private String authorFirstName;

  @Column(name = "AUTHOR_SURNAME")
  private String authorSurname;

	public PmcBlog() {
		// TODO Auto-generated constructor stub
	}
	
  public void addTag(PmcTag tag) {
    this.tags.add(tag);
  }

	@Override
	public Long getGuid() {
		// TODO Auto-generated method stub
		return super.getGuid();
	}
	
	@Override
	public void setGuid(Long guid) {
		// TODO Auto-generated method stub
		super.setGuid(guid);
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
  public String getTeaser() {
    return teaser;
  }

  public void setTeaser(String teaser) {
    this.teaser = teaser;
  }

  public Collection<PmcTag> getTags() {
    return tags;
  }

  public void setTags(Collection<PmcTag> tags) {
    this.tags = tags;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getPredecessor_id() {
    return predecessor_id;
  }

  public void setPredecessor_id(Long predecessor_id) {
    this.predecessor_id = predecessor_id;
  }

  public Date getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(Date publishDate) {
    this.publishDate = publishDate;
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

}
