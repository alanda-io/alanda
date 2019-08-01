package io.alanda.base.dto;

import java.util.List;
import java.util.Objects;


public class PmcRoleDto {
  
  private Long guid;
  
  //TODO: cleanup: remove idName,displayName when moved to new permission model
  private String idName;
  
  private String displayName;
  
  private String name;
  
  private String source;
  
  private List<PmcPermissionDto> permissions;
  
  
  public Long getGuid() {
    return guid;
  }

  
  public void setGuid(Long guid) {
    this.guid = guid;
  }

  
  public String getIdName() {
    return idName;
  }

  
  public void setIdName(String idName) {
    this.idName = idName;
  }

  
  public String getDisplayName() {
    return displayName;
  }

  
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<PmcPermissionDto> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<PmcPermissionDto> permissions) {
    this.permissions = permissions;
  }

  public String getSource() {
    if (source == null)
      return "";
    else 
      return source;
  }

  public void setSource(String source) {
    this.source = source;
  }
  
  @Override
  public String toString() {
    return "PmcRoleDto [guid=" + guid + ", idName=" + idName + ", displayName=" + displayName + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final PmcRoleDto other = (PmcRoleDto) obj;
    if (!Objects.equals(this.idName, other.idName)) {
      return false;
    }
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    return Objects.equals(this.guid, other.guid);
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 53 * hash + Objects.hashCode(this.guid);
    hash = 53 * hash + Objects.hashCode(this.idName);
    hash = 53 * hash + Objects.hashCode(this.name);
    return hash;
  }

  
}
