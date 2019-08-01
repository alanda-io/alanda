package io.alanda.base.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import io.alanda.base.util.UserContext;

@MappedSuperclass
public abstract class AbstractAuditEntity extends AbstractEntity {

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "CREATED")
  private Date createDate;

  @Column(name = "CREATEUSER")
  private Long createUser;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "LASTUPDATE")
  private Date updateDate;

  @Column(name = "UPDATEUSER")
  private Long updateUser;

  @PrePersist
  void onCreate() {
    this.setCreateDate(new Date());
    this.setCreateUser(UserContext.getUser().getGuid());
  }

  @PreUpdate
  void onUpdate() {
    this.setUpdateDate(new Date());
    this.setUpdateUser(UserContext.getUser().getGuid());
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public Long getCreateUser() {
    return createUser;
  }

  public void setCreateUser(Long createUser) {
    this.createUser = createUser;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }

  public Long getUpdateUser() {
    return updateUser;
  }

  void setUpdateUser(Long updateUser) {
    this.updateUser = updateUser;
  }

    
//  @Override
//  public boolean equals(Object obj) {
//    if (obj == null) {
//      return false;
//    }
//    if (obj == this) {
//      return true;
//    }
//    if (obj.getClass() != getClass()) {
//      return false;
//    }
//    AbstractAuditEntity rhs = (AbstractAuditEntity) obj;
//    return new EqualsBuilder()
//      .appendSuper(super.equals(obj))
//      .append(this.getGuid(), rhs.getGuid())
//      .append(this.getVersion(), rhs.getVersion())
//      .isEquals();
//  }
}
