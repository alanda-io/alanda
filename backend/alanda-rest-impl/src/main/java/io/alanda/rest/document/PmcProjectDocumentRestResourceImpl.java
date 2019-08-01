/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.rest.document;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.spi.HttpRequest;

import io.alanda.base.dto.DirectoryInfoDto;
import io.alanda.base.dto.DocuFolderDto;
import io.alanda.base.dto.DocuQueryDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.service.DocumentService;

import io.alanda.rest.security.DocumentAuthorizationService;

/**
 * @author developer
 */
public class PmcProjectDocumentRestResourceImpl implements RefObjectDocumentRestResource {

  protected static Log logger = LogFactory.getLog(PmcProjectDocumentRestResourceImpl.class);

  @Context
  HttpRequest request;

  @Inject
  private DocumentAuthorizationService documentAuthorizationService;

  @Inject
  private DocumentService documentService;

  private PmcProjectDto project;

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.rest.document.RefObjectDocumentRestResource#getTree(boolean)
   */
  @Override
  public DocuFolderDto getTree(boolean fileCount, String mappings) {

    List<DocuFolderDto> docuFolders = new ArrayList<>();
    if (mappings != null) {
      List<DocuQueryDto> qs = getDocuQueriesForMappings(mappings);
      for (DocuQueryDto dq : qs) {
        DirectoryInfoDto di = documentService.getTree(dq);
        String displayName = dq.displayName != null ? dq.displayName : null;
        if (displayName == null)
          displayName = dq.docuConfig != null ? dq.docuConfig.getDisplayName() : null;
        if (displayName == null)
          displayName = project.getProjectId();
        di.getConfig().getSourceFolder().setName(displayName);
        if ( !documentAuthorizationService.filterTreeByPermissions(di.getConfig().getSourceFolder(), null, project, di.getSubFolder())) {
          di.getConfig().setSourceFolder(null);
        }
        if (di.getConfig().getSourceFolder() != null) {
          // set mapping name to every folder for content queries
          setMappingsToTreeDirs(di.getConfig().getSourceFolder(), dq.primaryMappingName != null ? dq.primaryMappingName : dq.mappingName);
          docuFolders.add(di.getConfig().getSourceFolder());
        }
      }
    } else {
      DocuQueryDto qProject = DocuQueryDto.forPmcProject(project, true).withMappingName(project.getPmcProjectType().getIdName());
      qProject.fileCount = fileCount;
      logger.info("qProject=" + qProject);
      DirectoryInfoDto dirPr = documentService.getTree(qProject);
      dirPr.getConfig().getSourceFolder().setName(project.getProjectId());

      if ( !documentAuthorizationService
        .filterTreeByPermissions(dirPr.getConfig().getSourceFolder(), null, project, dirPr.getSubFolder())) {
        dirPr.getConfig().setSourceFolder(null);
      }
      docuFolders.add(dirPr.getConfig().getSourceFolder());

      DocuQueryDto qRefObject = DocuQueryDto
        .forRefObject(project.getRefObjectIdName(), project.getRefObjectType(), project.getRefObjectId(), fileCount);

      logger.info("qRefObject=" + qRefObject);
      DirectoryInfoDto dirRo = documentService.getTree(qRefObject);
      dirRo.getConfig().getSourceFolder().setName(project.getRefObjectIdName());

      if ( !documentAuthorizationService
        .filterTreeByPermissions(dirRo.getConfig().getSourceFolder(), dirRo.getRefObjectType(), null, dirRo.getSubFolder())) {
        dirRo.getConfig().setSourceFolder(null);
      }
      docuFolders.add(dirRo.getConfig().getSourceFolder());
    }
    return DocuFolderDto.virtual(null, "Project", docuFolders);
  }

  private void setMappingsToTreeDirs(DocuFolderDto df, String mapping) {
    df.setMapping(mapping);
    for (DocuFolderDto sub : df.getSubFolders()) {
      setMappingsToTreeDirs(sub, mapping);
    }
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.rest.document.RefObjectDocumentRestResource#getFolder(java.lang.Long)
   */
  @Override
  public FolderRestResource getFolder(Long folderId, String mappings) {
    String perm = getPermissionsForRequest(request);

    DocuQueryDto query = null;
    if (mappings != null) {
      List<DocuQueryDto> dqs = getDocuQueriesForMappings(mappings);
      for (DocuQueryDto dq : dqs) {
        DirectoryInfoDto di = documentService.getTree(dq);

        // ToDo: dieser accessCheck ist fehlerhaft!!!!
        if (di.hasId(folderId)) {
          query = dq;
        }
      }
      if (query == null) {
        throw new IllegalArgumentException("no query found for folder (folderId=" + folderId + ")");
      }
      //Fabian Check, habe versucht diese AccessQuery zu richten!!!
      query = query.withTargetFolderId(folderId);
      query.fileCount = false;
      DirectoryInfoDto di = documentService.getTree(query);
      documentAuthorizationService.checkPermissionForFolder(perm, null, project, di.getSubFolder());
    } else {
      query = DocuQueryDto.forPmcProject(project, false).withMappingName(project.getPmcProjectType().getIdName());

      DirectoryInfoDto dirPr = documentService.getTree(query);
      if (dirPr.hasId(folderId)) {
        query = query.withTargetFolderId(folderId);
        dirPr = documentService.getTree(query);
        documentAuthorizationService.checkPermissionForFolder(perm, null, project, dirPr.getSubFolder());
      } else {
        query = DocuQueryDto.forRefObject(project.getRefObjectIdName(), project.getRefObjectType(), project.getRefObjectId(), false);
        query = query.withTargetFolderId(folderId);
        dirPr = documentService.getTree(query);
        documentAuthorizationService.checkPermissionForFolder(perm, project.getRefObjectType(), null, dirPr.getSubFolder());
      }

    }
    return CDI.current().select(FolderRestResourceImpl.class).get().with(query);
  }

  @Override
  public FolderRestResource getFolderByName(String folderName, String mappings) {
    String perm = getPermissionsForRequest(request);

    DocuQueryDto query = null;
    if (mappings != null) {
      List<DocuQueryDto> dqs = getDocuQueriesForMappings(mappings);
      for (DocuQueryDto dq : dqs) {
        DirectoryInfoDto di = documentService.getTree(dq);

        // ToDo: dieser accessCheck ist fehlerhaft!!!!
        if (di.hasName(folderName)) {
          query = dq;
        }
      }
      if (query == null) {
        throw new IllegalArgumentException("no query found for folder (folderName=" + folderName + ")");
      }
      //Fabian Check, habe versucht diese AccessQuery zu richten!!!
      query = query.withTargetFolderName(folderName);
      query.fileCount = false;
      DirectoryInfoDto di = documentService.getTree(query);
      documentAuthorizationService.checkPermissionForFolder(perm, null, project, di.getSubFolder());
    } else {

      query = DocuQueryDto.forPmcProject(project, false).withMappingName(project.getPmcProjectType().getIdName());

      DirectoryInfoDto dirPr = documentService.getTree(query);
      documentAuthorizationService.checkPermissionForFolder(perm, null, project, dirPr.getSubFolder());
      if ( !dirPr.hasName(folderName)) {

        query = DocuQueryDto.forRefObject(project.getRefObjectIdName(), project.getRefObjectType(), project.getRefObjectId(), false);
      }
      logger.info("using Query=" + query);
    }

    return CDI.current().select(FolderRestResourceImpl.class).get().with(query.withTargetFolderName(folderName));
  }

  private List<DocuQueryDto> getDocuQueriesForMappings(String mappings) {
    List<DocuQueryDto> result = new ArrayList<>();
    String[] mappingArray = StringUtils.split(mappings, ",");
    mappingArray = StringUtils.stripAll(mappingArray);
    for (String mapping : mappingArray) {
      result.addAll(documentService.resolveMappingByProject(project, mapping));
    }
    return result;
  }

  public RefObjectDocumentRestResource with(PmcProjectDto project) {
    this.project = project;
    return this;
  }

}
