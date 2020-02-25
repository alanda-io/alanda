/** */
package io.alanda.base.util;

import io.alanda.base.dto.PmcGroupDto;
import io.alanda.base.dto.PmcRoleDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.process.PmcProjectData;
import io.alanda.base.service.PmcGroupService;
import io.alanda.base.service.PmcProjectService;
import io.alanda.base.service.PmcPropertyService;
import io.alanda.base.service.PmcRoleService;
import io.alanda.base.service.PmcUserService;
import io.alanda.base.util.cache.UserCache;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/** @author jlo */
@Named("grp")
@ApplicationScoped
public class ProcessGroupConnector {

  @Inject protected PmcUserService pmcUserService;

  @Inject
  @Named("pmcProjectData")
  protected PmcProjectData pmcProjectData;

  @Inject protected PmcPropertyService propertyService;

  @Inject protected PmcProjectService projectService;

  @Inject protected PmcRoleService roleService;

  @Inject protected PmcGroupService groupService;

  @Inject protected UserCache userCache;

  public String g(String groupName) {
    PmcGroupDto g = this.pmcUserService.getGroupByName(groupName);
    return g.getGuid().toString();
  }

  public String gForRole(String roleName) {
    String groupId =
        propertyService.getStringProperty(
            null, null, pmcProjectData.getPmcProjectGuid(), "role_" + roleName);
    Long lId = Long.parseLong(groupId);
    PmcGroupDto g = this.pmcUserService.getGroupById(lId);
    return g.getGuid().toString();
  }

  /**
   * Check if a project specific role is set in the propertyStore and then return all groups for that role.
   * If no project specific role is set, the method simply returns all groups for that role.
   * @param roleName
   * @return list of groups
   */
  public List<String> groupsForRole(String roleName) {
    List<String> groupsResult = new ArrayList<>();
    String groupId =
            propertyService.getStringProperty(
                    null, null, pmcProjectData.getPmcProjectGuid(), "role_" + roleName);
    if (groupId != null) {
      Long lId = Long.parseLong(groupId);
      PmcGroupDto g = this.pmcUserService.getGroupById(lId);
      groupsResult.add(g.getGuid().toString());
    } else {
      PmcRoleDto role = this.roleService.getRole(roleName);
      if(role != null) {
        List<PmcGroupDto> pmcGroupDtos = this.groupService.getGroupsForRole(role.getGuid());
        for (PmcGroupDto pmcGroupDto : pmcGroupDtos) {
          groupsResult.add(pmcGroupDto.getGuid().toString());
        }
      }
    }
    return groupsResult;
  }

  public String gForRoleNew(String roleName) {
    String groupId =
        propertyService.getStringProperty(
            null, null, pmcProjectData.getPmcProjectGuid(), "role_" + roleName);
    if (groupId != null) {
      Long lId = Long.parseLong(groupId);
      PmcGroupDto g = this.pmcUserService.getGroupById(lId);
      return g.getGuid().toString();
    }
    return "";
  }

  public String aByRole(String roleName) {
    Long pmcProjectGuid = pmcProjectData.getPmcProjectGuid();
    if (pmcProjectGuid == null)
      throw new RuntimeException("No pmcProjectGuid found in the process variables!");

    String assigneeId =
        propertyService.getStringProperty(null, null, pmcProjectGuid, "role_" + roleName);
    Long id = Long.valueOf(assigneeId);
    if (id == null) return "";
    //      throw new RuntimeException(
    //          "No assignee with role "
    //              + roleName
    //              + " for pmcProjectGuid "
    //              + pmcProjectGuid
    //              + " found!");

    PmcUserDto u = this.pmcUserService.getUserByUserId(id);
    if (u == null) throw new RuntimeException("No user with id " + id + " found!");

    return u.getGuid().toString();
  }

  public String gByProp(String propName) {
    Long pmcProjectGuid = pmcProjectData.getPmcProjectGuid();
    if (pmcProjectGuid == null)
      throw new RuntimeException("No pmcProjectGuid found in the process variables!");

    String groupName = propertyService.getStringProperty(null, null, pmcProjectGuid, propName);
    if (groupName == null)
      throw new RuntimeException(
          "No property with name "
              + propName
              + " for pmcProjectGuid "
              + pmcProjectGuid
              + " found!");

    PmcGroupDto g = this.pmcUserService.getGroupByName(groupName);
    if (g == null) throw new RuntimeException("No group with name " + groupName + " found!");

    return g.getGuid().toString();
  }

  public String gByVar(String varName) {
    Long pmcProjectGuid = pmcProjectData.getPmcProjectGuid();
    if (pmcProjectGuid == null)
      throw new RuntimeException("No pmcProjectGuid found in the process variables!");

    String groupName = pmcProjectData.getVariable(varName);
    if (groupName == null)
      throw new RuntimeException(
          "No variable with name " + varName + " for pmcProjectGuid " + pmcProjectGuid + " found!");

    PmcGroupDto g = this.pmcUserService.getGroupByName(groupName);
    if (g == null) throw new RuntimeException("No group with name " + groupName + " found!");

    return g.getGuid().toString();
  }

  public String aByProp(String propName) {
    Long pmcProjectGuid = pmcProjectData.getPmcProjectGuid();
    if (pmcProjectGuid == null)
      throw new RuntimeException("No pmcProjectGuid found in the process variables!");

    String assigneeName = propertyService.getStringProperty(null, null, pmcProjectGuid, propName);
    if (assigneeName == null)
      throw new RuntimeException(
          "No property with name "
              + propName
              + " for pmcProjectGuid "
              + pmcProjectGuid
              + " found!");

    PmcUserDto u = this.pmcUserService.getUserByLoginName(assigneeName);
    if (u == null) throw new RuntimeException("No user with loginname " + assigneeName + " found!");

    return u.getGuid().toString();
  }

  public String aByUT(String utId) {
    String assigneeName =
        propertyService.getStringProperty(null, "USER_TASK_" + utId, null, "assignee");
    if (assigneeName == null)
      throw new RuntimeException("No assignee property for UT" + utId + " found!");

    PmcUserDto u = this.pmcUserService.getUserByLoginName(assigneeName);
    if (u == null) throw new RuntimeException("No user with loginname " + assigneeName + " found!");

    return u.getGuid().toString();
  }

  public String aByLoginName(String loginName) {
    PmcUserDto user = this.pmcUserService.getUserByLoginName(loginName);
    return user != null ? user.getGuid().toString() : null;
  }

  public String a(String roleName) {
    Long pmcProjectGuid = pmcProjectData.getPmcProjectGuid();
    Long roleConsumer =
        roleService.getGroupRoleInstance(pmcProjectGuid, roleName).getRoleConsumer();
    return roleConsumer != null ? roleConsumer.toString() : null;
  }

  public String ap(String roleName) {
    Long pmcProjectGuid = pmcProjectData.getPmcProjectGuid();
    Long refObjectId = pmcProjectData.getRefObjectId();
    String refObjectType = pmcProjectData.getRefObjectType();
    PmcUserDto assignee =
        projectService.getUserForRole(refObjectType, refObjectId, roleName, pmcProjectGuid);
    if (assignee == null) {
      return null;
    }
    return assignee.getGuid().toString();
  }
}
