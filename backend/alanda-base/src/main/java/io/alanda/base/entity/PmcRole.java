/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 *
 * @author developer
 */
@Entity
@Table(name = "PMC_ROLE")
@NamedQueries({@NamedQuery(name = "PmcRole.getRoleByName", query = "Select r from PmcRole r where r.name = :name")})
public class PmcRole extends AbstractEntity implements Serializable {
  
  private static final long serialVersionUID = 7816191306451033343L;
   
  private String name;
  
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "PMC_ROLE_PERMISSION", joinColumns = @JoinColumn(name = "REF_ROLE", referencedColumnName = "GUID"), inverseJoinColumns = @JoinColumn(name = "REF_PERMISSION", referencedColumnName = "GUID"))
  private List<PmcPermission> permissions;

  public PmcRole() {
  }

  public PmcRole(String name, List<PmcPermission> permissions) {
    this.name = name;
    this.permissions = permissions;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<PmcPermission> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<PmcPermission> permissions) {
    this.permissions = permissions;
  }
  
  
  
}
