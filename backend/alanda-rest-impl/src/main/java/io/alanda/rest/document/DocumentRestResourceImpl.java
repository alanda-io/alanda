/**
 * 
 */
package io.alanda.rest.document;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.rest.exception.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.DocuQueryDto;
import io.alanda.base.dto.DocumentHistoryDto;
import io.alanda.base.dto.DocumentSimpleDto;
import io.alanda.base.service.DocumentService;

/**
 * @author jlo
 */
public class DocumentRestResourceImpl implements DocumentRestResource {

  private static final Logger log = LoggerFactory.getLogger(DocumentRestResourceImpl.class);

  @Inject
  private DocumentService documentService;

  private DocuQueryDto query;

  private String documentGuid;

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.rest.document.DocumentRestResource#download(boolean)
   */
  @Override
  public Response download(boolean inline) throws IOException {
    log.info("Download called.: {}, guid: {}", query, documentGuid);
    if (documentGuid == null) {
      throw new InvalidRequestException(Status.BAD_REQUEST, "query parameter documentGuid was not provided");
    }

    DocumentSimpleDto doc = documentService.get(documentGuid);
    final InputStream is = doc.getIntputStream();

    StreamingOutput stream = new StreamingOutput() {

      @Override
      public void write(OutputStream output) throws IOException, WebApplicationException {
        try {
          output.write(IOUtils.toByteArray(is));
          is.close();
          output.close();
        } catch (Exception e) {
          throw new WebApplicationException(e);
        }
      }
    };

    if ( !inline) {
      return Response
        .ok(stream, MediaType.APPLICATION_OCTET_STREAM)
        .header("content-disposition", "attachment; filename=\"" + doc.getName() + "\"")
        .header("Content-Length", is.available())
        .build();
    } else {
      return Response.ok(stream, doc.getMediaType()).header("Content-Length", is.available()).build();
    }
  }

  @Override
  public Response downloadFromHistory(String versionString, boolean inline) throws IOException {
    log.info("DownloadFromHistory called.: {}, guid: {}, version: {}.", query, documentGuid, versionString);
    if (documentGuid == null) {
      throw new InvalidRequestException(Status.BAD_REQUEST, "query parameter documentGuid was not provided");
    }

    DocumentSimpleDto doc = documentService.getFromHistory(documentGuid, versionString);
    final InputStream is = doc.getIntputStream();

    StreamingOutput stream = new StreamingOutput() {

      @Override
      public void write(OutputStream output) throws IOException, WebApplicationException {
        try {
          output.write(IOUtils.toByteArray(is));
          is.close();
          output.close();
        } catch (Exception e) {
          throw new WebApplicationException(e);
        }
      }
    };

    if ( !inline) {
      return Response
        .ok(stream, MediaType.APPLICATION_OCTET_STREAM)
        .header("content-disposition", "attachment; filename=\"" + doc.getName() + "\"")
        .header("Content-Length", is.available())
        .build();
    } else {
      return Response.ok(stream, doc.getMediaType()).header("Content-Length", is.available()).build();
    }
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.rest.document.DocumentRestResource#delete()
   */
  @Override
  public Response delete() throws IOException {
    if (documentGuid == null) {
      throw new InvalidRequestException(Status.BAD_REQUEST, "query parameter documentGuid was not provided");
    }
    documentService.delete(documentGuid);

    return Response.status(200).build();
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.rest.document.DocumentRestResource#rename(java.lang.String)
   */
  @Override
  public Response rename(String newFileName) throws IOException {
    if (documentGuid == null) {
      throw new InvalidRequestException(Status.BAD_REQUEST, "query parameter documentGuid was not provided");
    }

    if (newFileName == null) {
      throw new InvalidRequestException(Status.BAD_REQUEST, "query parameter newFileName was not provided");
    }

    documentService.rename(documentGuid, newFileName);
    return Response.status(200).build();
  }

  public DocumentRestResourceImpl with(DocuQueryDto query, String documentGuid) {
    this.query = query;
    this.documentGuid = documentGuid;
    return this;
  }



  @Override
  public List<DocumentHistoryDto> showHistory() {
    return documentService.showHistory(documentGuid);
  }

}
