/**
 * 
 */
package io.alanda.rest.document;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import io.alanda.base.service.DocumentService;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;

import io.alanda.base.connector.PmcRefObjectConnector;
import io.alanda.base.dto.DocuQueryDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.RefObject;
import io.alanda.base.service.PmcProjectService;
import io.alanda.base.type.ProcessVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jlo
 */
public class DocumentRestServiceImpl implements DocumentRestService {

  private static final Logger log = LoggerFactory.getLogger(DocumentRestServiceImpl.class);

  @Inject
  private PmcProjectService projectService;

  @Inject
  private DocumentService documentService;

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private HistoryService historyService;

  List<String> varNamesForProcessRestResource = Arrays.asList(ProcessVariables.REFOBJECTTYPE, ProcessVariables.REFOBJECTID);

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.rest.document.DocumentRestService#getRefObjectResource(java.lang.String, java.lang.Long)
   */
  @Override
  public RefObjectDocumentRestResource getRefObjectResource(String refObjectType, String stringObjectId) {
    log.info("refObjectType={}, refObjectId={}", refObjectType, stringObjectId);
    if ("project".equals(refObjectType)) {
      Long refObjectId = Long.parseLong(stringObjectId);
      PmcProjectDto project = projectService.getProjectByGuid(refObjectId);

      return CDI.current().select(PmcProjectDocumentRestResourceImpl.class).get().with(project);
    } else if ("process".equals(refObjectType)) {
      return getProcessResource(stringObjectId);
    } else {
      Long refObjectId = Long.parseLong(stringObjectId);
      PmcRefObjectConnector loader = projectService.getRefObjectLoader(refObjectType);
      if (loader == null) {
        throw new IllegalArgumentException("No refObjectType '" + refObjectType + "' registered.");
      }
      RefObject ro = loader.getRefObjectById(refObjectId);
      DocuQueryDto q = DocuQueryDto.forRefObject(ro.getIdName(), refObjectType, refObjectId, false);
      return CDI.current().select(RefObjectDocumentRestResourceImpl.class).get().with(q);
    }
  }

  @Override
  public Response deleteDocument(String documentGuid) throws IOException {
    if(StringUtils.isBlank(documentGuid)){
      return Response.status(HttpStatus.SC_BAD_REQUEST).build();
    }
    documentService.delete(documentGuid);
    return Response.status(HttpStatus.SC_NO_CONTENT).build();
  }

  private RefObjectDocumentRestResource getProcessResource(String processInstanceId) {
    Map<String, Object> varMap = runtimeService.getVariables(processInstanceId, varNamesForProcessRestResource);
    String refObjectType = (String) varMap.get(ProcessVariables.REFOBJECTTYPE);
    Long refObjectId = (Long) varMap.get(ProcessVariables.REFOBJECTID);
    PmcRefObjectConnector loader = projectService.getRefObjectLoader(refObjectType);
    if (loader == null) {
      throw new IllegalArgumentException("No refObjectType '" + refObjectType + "' registered.");
    }
    RefObject ro = loader.getRefObjectById(refObjectId);
    DocuQueryDto q = DocuQueryDto.forRefObject(ro.getIdName(), refObjectType, refObjectId, false);
    return CDI.current().select(RefObjectDocumentRestResourceImpl.class).get().with(q);
  }

  //  @Override
  //  public PmcProjectDocumentRestResource getPmcProjectResource(Long projectGuid) {
  //    PmcProjectDto project = projectService.getProjectByGuid(projectGuid);
  //    DocuQueryDto q = DocuQueryDto.forPmcProject(project, true);
  //    return CDI.current().select(PmcProjectDocumentRestResourceImpl.class).get().with(q);
  //  }

}
