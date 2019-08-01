/**
 * 
 */
package io.alanda.base.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * @author jlo
 */
@Entity
@Table(name = "PMC_GROUP")
@NamedQueries({@NamedQuery(name = "PmcGroup.getByGroupName", query = "Select g from PmcGroup g where g.groupName = :groupName")})
@NamedNativeQueries({
  @NamedNativeQuery(name = "PmcGroup.findByRole", query = "select g.* FROM PMC_GROUP g, PMC_GROUP_ROLE gr where g.guid=gr.REF_GROUP and gr.ref_role=:roleId", resultClass = PmcGroup.class),
  @NamedNativeQuery(name = "PmcGroup.findByRolename", query = "select g.* FROM PMC_GROUP g, PMC_GROUP_ROLE gr, pmc_role r where g.guid=gr.REF_GROUP and r.guid=gr.ref_role and r.name=:roleName", resultClass = PmcGroup.class)
})

public class PmcGroup extends AbstractAuditEntity implements Serializable {

  private static final long serialVersionUID = -4980837619559876933L;

  private String groupName;

  private String longName;

  private String email;

  private String phone;

  private boolean active;

  private String groupSource;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "PMC_GROUP_PERMISSION", joinColumns = @JoinColumn(name = "REF_GROUP", referencedColumnName = "GUID"), inverseJoinColumns = @JoinColumn(name = "REF_PERMISSION", referencedColumnName = "GUID"))
  private List<PmcPermission> permissions;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "PMC_GROUP_ROLE", joinColumns = @JoinColumn(name = "REF_GROUP", referencedColumnName = "GUID"), inverseJoinColumns = @JoinColumn(name = "REF_ROLE", referencedColumnName = "GUID"))
  private List<PmcRole> roles;

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String getLongName() {
    return longName;
  }

  public void setLongName(String longName) {
    this.longName = longName;
  }

  public String getGroupSource() {
    return groupSource;
  }

  public void setGroupSource(String groupSource) {
    this.groupSource = groupSource;
  }

  public List<PmcPermission> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<PmcPermission> permissions) {
    this.permissions = permissions;
  }

  public List<PmcRole> getRoles() {
    return roles;
  }

  public void setRoles(List<PmcRole> roles) {
    this.roles = roles;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

}
