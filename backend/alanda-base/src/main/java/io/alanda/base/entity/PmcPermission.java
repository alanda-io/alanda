/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author developer
 */
@Entity
@Table(name = "PMC_PERMISSION")
@NamedQueries({@NamedQuery(name = "PmcPermission.getPermissionByKey", query = "Select p from PmcPermission p where p.key = :key")})
public class PmcPermission extends AbstractEntity implements Serializable {
  
  private static final long serialVersionUID = 5883304856516003492L;
  
  private String key;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
  
}
