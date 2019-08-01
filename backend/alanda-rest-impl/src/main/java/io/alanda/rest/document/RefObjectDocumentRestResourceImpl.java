/**
 * 
 */
package io.alanda.rest.document;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.spi.HttpRequest;

import io.alanda.base.dto.DirectoryInfoDto;
import io.alanda.base.dto.DocuFolderDto;
import io.alanda.base.dto.DocuQueryDto;
import io.alanda.base.service.DocumentService;

import io.alanda.rest.security.DocumentAuthorizationService;

/**
 * @author jlo
 */
public class RefObjectDocumentRestResourceImpl implements RefObjectDocumentRestResource {

  protected static Log logger = LogFactory.getLog(RefObjectDocumentRestResourceImpl.class);

  @Context
  HttpRequest request;

  @Inject
  private DocumentAuthorizationService documentAuthorizationService;

  @Inject
  private DocumentService documentService;

  DocuQueryDto query;

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.rest.document.RefObjectDocumentRestResource#getTree(boolean)
   */
  @Override
  public DocuFolderDto getTree(boolean fileCount, String mappings) {
    logger.info("query=" + query);
    query.fileCount = fileCount;
    DirectoryInfoDto dir = documentService.getTree(query);
    DocuFolderDto ret = dir.getConfig().getSourceFolder();
    if ( !documentAuthorizationService.filterTreeByPermissions(ret, dir.getRefObjectType(), null, dir.getSubFolder())) {
      throw new ForbiddenException("You are not allowed to read this folder!");
    }
    return ret;
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.rest.document.RefObjectDocumentRestResource#getFolder(java.lang.Long)
   */
  @Override
  public FolderRestResource getFolder(Long folderId, String mappings) {
    String perm = getPermissionsForRequest(request);
    DirectoryInfoDto dir = documentService.getTree(query.withTargetFolderId(folderId));
    documentAuthorizationService.checkPermissionForFolder(perm, dir.getRefObjectType(), null, dir.getSubFolder());
    return CDI.current().select(FolderRestResourceImpl.class).get().with(query.withTargetFolderId(folderId));
  }

  @Override
  public FolderRestResource getFolderByName(String folderName, String mappings) {
    String perm = getPermissionsForRequest(request);
    DirectoryInfoDto dir = documentService.getTree(query.withTargetFolderName(folderName));
    documentAuthorizationService.checkPermissionForFolder(perm, dir.getRefObjectType(), null, dir.getSubFolder());
    return CDI.current().select(FolderRestResourceImpl.class).get().with(query.withTargetFolderName(folderName));
  }

  public RefObjectDocumentRestResourceImpl with(DocuQueryDto query) {
    this.query = query;
    return this;
  }

}
