package io.alanda.base.dto;


public class PmcRoleInstanceDto {
  
  private Long guid;
  
  private PmcRoleDto role;
  
  private String roleEntity;
  
  private String roleEntityType;
  
  private Long roleConsumer;
  
  private String roleConsumerType;

  public static PmcRoleInstanceDto forRoleAndUser(Long userId, String roleName) {
    PmcRoleDto rod = new PmcRoleDto();
    rod.setIdName(roleName);
    PmcRoleInstanceDto rid = new PmcRoleInstanceDto();
    rid.setRoleConsumer(userId);
    rid.setRole(rod);
    return rid;
  }

  
  public Long getGuid() {
    return guid;
  }

  
  public void setGuid(Long guid) {
    this.guid = guid;
  }

  
  public String getRoleEntity() {
    return roleEntity;
  }

  
  public void setRoleEntity(String roleEntity) {
    this.roleEntity = roleEntity;
  }

  
  public String getRoleEntityType() {
    return roleEntityType;
  }

  
  public void setRoleEntityType(String roleEntityType) {
    this.roleEntityType = roleEntityType;
  }

  
  public Long getRoleConsumer() {
    return roleConsumer;
  }

  
  public void setRoleConsumer(Long roleConsumer) {
    this.roleConsumer = roleConsumer;
  }

  
  public String getRoleConsumerType() {
    return roleConsumerType;
  }

  
  public void setRoleConsumerType(String roleConsumerType) {
    this.roleConsumerType = roleConsumerType;
  }

  public PmcRoleDto getRole() {
    return role;
  }

  
  public void setRole(PmcRoleDto role) {
    this.role = role;
  }
  
  @Override
  public String toString() {
    return "PmcRoleInstanceDto [guid=" +
      guid +
      ", role=" +
      role +
      ", roleEntity=" +
      roleEntity +
      ", roleEntityType=" +
      roleEntityType +
      ", roleConsumer=" +
      roleConsumer +
      ", roleConsumerType=" +
      roleConsumerType +
      "]";
  }

}
