/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.security;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.service.PmcProjectService;
import io.alanda.base.service.PmcPropertyService;

/**
 * @author developer
 */
@ApplicationScoped
public class PmcShiroAuthorizationService {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  //TODO: replace with Cache
  private final HashMap<Long, String> projectKeys = new HashMap<>();

  @Inject
  private PmcProjectService pmcProjectService;

  @Inject
  private PmcPropertyService pmcPropertyService;

  @Inject
  private Instance<ProjectTypeAuthLoader> projectTypeAuthLoaders;

  private Map<String, ProjectTypeAuthLoader> authLoaders;

  @PostConstruct
  private void initAuthService() {
    this.authLoaders = new HashMap<>();
    for (ProjectTypeAuthLoader projectTypeAuthLoader : projectTypeAuthLoaders) {
      if (projectTypeAuthLoader.getTypeNames() != null) {
        for (String typeName : projectTypeAuthLoader.getTypeNames()) {
          authLoaders.put(typeName, projectTypeAuthLoader);
        }
      }
    }
  }

  public boolean checkPermission(String permission) {
    Subject subject = SecurityUtils.getSubject();
    return subject.isPermitted(permission);
  }

  public PmcProjectDto checkPermissionsForProject(PmcProjectDto project, String permissions, boolean checkRelatives) {
    return checkPermissionsForProject(project, permissions, null, checkRelatives);
  }

  public PmcProjectDto checkPermissionsForProject(PmcProjectDto project, String permissions, String suffix, boolean checkRelatives) {
    Subject subject = SecurityUtils.getSubject();
    if ( !subject.isPermitted(getAuthKeyForProject(project, permissions, suffix))) {
      return null;
    }
    project.setAuthBase(getAuthBaseForProject(project.getGuid()));

    if (checkRelatives) {
      checkPermissionsOfRelatives(project, permissions, true, false);
      checkPermissionsOfRelatives(project, permissions, false, true);
    }

    return project;
  }

  private void checkPermissionsOfRelatives(PmcProjectDto project, String permissions, boolean checkParents, boolean checkChildren) {
    if (checkChildren) {
      Collection<PmcProjectDto> children = project.getChildren();
      if (children != null) {
        checkPermissionsForProjects(children, permissions);
        for (PmcProjectDto child : children) {
          checkPermissionsOfRelatives(child, permissions, checkParents, checkChildren);
        }
      }
    }

    if (checkParents) {
      Collection<PmcProjectDto> parents = project.getParents();
      if (parents != null) {
        checkPermissionsForProjects(parents, permissions);
        for (PmcProjectDto parent : parents) {
          checkPermissionsOfRelatives(parent, permissions, checkParents, checkChildren);
        }
      }
    }
  }

  private Collection<PmcProjectDto> checkPermissionsForProjects(Collection<PmcProjectDto> projects, String permissions) {

    Subject subject = SecurityUtils.getSubject();
    Iterator<PmcProjectDto> iter = projects.iterator();
    while (iter.hasNext()) {
      PmcProjectDto project = iter.next();
      if ( !subject.isPermitted(getAuthKeyForProject(project, permissions))) {
        iter.remove();
      } else {
        project.setAuthBase(getAuthBaseForProject(project.getGuid()));
      }
    }
    return projects;
  }

  public boolean checkPermissionsForPhase(PmcProjectDto project, String phaseDefinitionIdName, String permissions) {
    PmcProjectDto result = checkPermissionsForProject(project, permissions, "phase:" + phaseDefinitionIdName, false);
    return result != null;
  }

  public String getAuthBaseForProject(Long projectGuid) {
    //    String key = projectKeys.get(projectGuid);
    String key = null;
    if (key == null) {
      PmcProjectDto project = pmcProjectService.getProjectByGuid(projectGuid);
      key = addOrUpdateBaseAuthKeyForProject(project);
    }
    return key;
  }

  public String getAuthBaseForProject(PmcProjectDto project) {
    //    String key = projectKeys.get(project.getGuid());
    String key = null;
    if (key == null) {
      key = addOrUpdateBaseAuthKeyForProject(project);
    }
    return key;
  }

  public String getAuthKeyForProject(Long guid, String permissions) {
    String base = "project:" + getAuthBaseForProject(guid);
    Map<String, String> params = new HashMap<>();
    params.put("permissions", permissions);
    return evaluateTemplate(base, params);
  }

  public String getAuthKeyForProject(PmcProjectDto project, String permissions) {
    return getAuthKeyForProject(project, permissions, null);
  }

  public String getAuthKeyForProject(PmcProjectDto project, String permissions, String suffix) {
    String base = "project:" + getAuthBaseForProject(project);
    if (suffix != null) {
      base += ":" + suffix;
    }
    Map<String, String> params = new HashMap<>();
    params.put("permissions", permissions);
    return evaluateTemplate(base, params);
  }

  public String getAuthKeyForMilestone(Long projectGuid, String milestoneIdName, String permissions) {
    String base = "ms:" + getAuthBaseForProject(projectGuid) + ":" + milestoneIdName;
    Map<String, String> params = new HashMap<>();
    params.put("permissions", permissions);
    return evaluateTemplate(base, params);
  }

  public String getAuthKeyForProperty(Long projectGuid, String propertyKey, String permissions) {
    String base = "prop:" + getAuthBaseForProject(projectGuid) + ":" + propertyKey;
    Map<String, String> params = new HashMap<>();
    params.put("permissions", permissions);
    return evaluateTemplate(base, params);
  }

  public String addOrUpdateBaseAuthKeyForProject(PmcProjectDto project) {
    String key = "#{permissions}:" + project.getPmcProjectType().getIdName();
    if ((project.getActivePhases() == null) || (project.getActivePhases().length == 0)) {
      key += ":none";
    } else {
      key += ":";
      for (int i = 0; i < project.getActivePhases().length; i++ ) {
        key += project.getActivePhases()[i];
        if (i < project.getActivePhases().length - 1)
          key += ",";
      }
    }
    if (project.getStatus() == null) {
      key += ":none";
    } else {
      key += ":" + project.getStatus();
    }
    ProjectTypeAuthLoader authLoader = authLoaders.get(project.getPmcProjectType().getIdName());
    if (authLoader != null) {
      key += ":" + authLoader.getKeyForType(project);
    } else {
      key += ":none";
    }
    projectKeys.put(project.getGuid(), key);
    logger.info("AuthKey for Project #" + project.getProjectId() + "(" + project.getGuid() + "): " + key);
    return key;
  }

  public void invalidateAuthKey(Long projectGuid) {
    projectKeys.remove(projectGuid);
  }

  private String evaluateTemplate(String template, Map<String, String> data) {

    StrSubstitutor subst = new StrSubstitutor(data);
    subst.setVariablePrefix("#{");
    String ret = subst.replace(template);
    return ret;

  }

}
