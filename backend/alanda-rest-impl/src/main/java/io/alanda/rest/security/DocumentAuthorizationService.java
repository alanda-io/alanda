/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.rest.security;

import java.util.Iterator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.DocuFolderDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.security.PmcShiroAuthorizationService;

/**
 * @author developer
 */
@ApplicationScoped
public class DocumentAuthorizationService {

  private static final Logger log = LoggerFactory.getLogger(DocumentAuthorizationService.class);

  @Inject
  private PmcShiroAuthorizationService pmcShiroAuthorizationService;

  public boolean filterTreeByPermissions(DocuFolderDto tree, String objectType, PmcProjectDto project, String subFolder) {
    String authKey = getAuthKeyForFolder("read", objectType, project, subFolder);
    if ( !pmcShiroAuthorizationService.checkPermission(authKey)) {
      return false;
    } else {
      authKey = getAuthKeyForFolder("delete", objectType, project, subFolder);
      if (pmcShiroAuthorizationService.checkPermission(authKey)) {
        tree.setPermissions(tree.getPermissions() + "wd");
      } else {
        authKey = getAuthKeyForFolder("write", objectType, project, subFolder);
        if (pmcShiroAuthorizationService.checkPermission(authKey)) {
          tree.setPermissions(tree.getPermissions() + "w");
        }
      }
      Iterator<DocuFolderDto> i = tree.getSubFolders().iterator();
      while (i.hasNext()) {
        DocuFolderDto sub = i.next();
        if ( !filterTreeByPermissions(sub, objectType, project, subFolder + sub.getName() + "/")) {
          i.remove();
        }
      }
      return true;
    }
  }

  public void checkPermissionForFolder(String perm, String refObjectType, PmcProjectDto project, String subFolder) {
    String authKey = getAuthKeyForFolder(perm, refObjectType, project, subFolder);
    if ( !pmcShiroAuthorizationService.checkPermission(authKey)) {
      log.info("AccessDenied: Type: {}, refObjectType: {},project: {},  folder: {}, auth: {}", perm, refObjectType, project != null ? project.getProjectId() : "[null]", subFolder, authKey);
      throw new ForbiddenException("You are not allowed to " + perm + "in this folder!");
    }
  }

  public String getAuthKeyForFolder(String perm, String refObjectType, PmcProjectDto project, String subFolder) {
    String ret = "folder:";
    if (project != null) {
      ret += pmcShiroAuthorizationService.getAuthKeyForProject(project, perm);
    } else {
      ret += "refobject:" + perm + ":" + refObjectType;
    }
    ret += ":" + subFolder;
    return ret;
  }

}
