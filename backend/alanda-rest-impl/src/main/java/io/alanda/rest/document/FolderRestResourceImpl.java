/**
 * 
 */
package io.alanda.rest.document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.DocuQueryDto;
import io.alanda.base.dto.DocumentSimpleDto;
import io.alanda.base.service.DocumentService;
import io.alanda.base.util.DocumentUtils;
import io.alanda.base.util.UserContext;

/**
 * @author jlo
 */
public class FolderRestResourceImpl implements FolderRestResource {

  private static final Logger logger = LoggerFactory.getLogger(FolderRestResourceImpl.class);

  @Inject
  private DocumentService documentService;

  private DocuQueryDto query;

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.rest.document.RefObjectDocumentRestResource#downloadAll()
   */
  @Override
  public Response downloadAll() throws IOException {
    StreamingOutput stream = new StreamingOutput() {

      @Override
      public void write(OutputStream output) throws IOException, WebApplicationException {
        try {

          documentService.getAll(query, output);
        } catch (Exception e) {
          throw new WebApplicationException(e);
        }
      }
    };
    String type = (String) query.paramMap.getOrDefault("refObjectType", "type");
    String idName = (String) query.paramMap.getOrDefault("refObjectIdName", "idName");
    return Response
      .ok(stream, MediaType.APPLICATION_OCTET_STREAM)
      .header("content-disposition", "attachment; filename=\"" + type + "_" + idName + ".zip\"")
      //.header("Content-Length", ) need to create temp file?
      .build();
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.rest.document.FolderRestResource#getFolderContent()
   */
  @Override
  public List<DocumentSimpleDto> getFolderContent(String fileMask) {

    List<DocumentSimpleDto> files = documentService.getFolderContent(query);

    if (fileMask == null)
      return files;

    WildcardFileFilter wcf = new WildcardFileFilter(fileMask, IOCase.INSENSITIVE);
    files = files
      .stream()
      .filter(f -> wcf.accept(new File(f.getName())))
      .sorted((f1, f2) -> compareFileDates(f1, f2))
      .collect(Collectors.toList());
    if (files.size() > 0) {
      return files;
    }
    return Collections.EMPTY_LIST;
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.rest.document.FolderRestResource#upload(org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput)
   */
  @Override
  public Response upload(MultipartFormDataInput input, String filename) throws Exception {
    List<DocumentSimpleDto> uploadPaths = new LinkedList<>();

    List<InputPart> files = input.getParts();
    for (InputPart file : files) {
      MultivaluedMap<String, String> header = file.getHeaders();
      filename = DocumentUtils.getFileName(header);
      MediaType mt = DocumentUtils.getMediaTypeByFilename(filename);
      if (mt == null) {
        mt = file.getMediaType();
      }
      InputStream inputStream = file.getBody(InputStream.class, null);
      byte[] bytes = IOUtils.toByteArray(inputStream);
      inputStream.close();
      DocumentSimpleDto fi = new DocumentSimpleDto(filename, mt.getType() + "/" + mt.getSubtype(), (long) bytes.length);
      fi.setAuthorName(UserContext.getUser().getDisplayName());
      DocumentSimpleDto doc = documentService.store(query, bytes, fi);
      logger.info("Upload von " + doc.getName() + ", auf: " + doc.getPath());
      uploadPaths.add(doc);
    }
    //    Map<String, List<InputPart>> formData = input.getFormDataMap();
    //    if (formData.containsKey("file")) {
    //      List<InputPart> inputParts = formData.get("file");
    //      for (InputPart inputPart : inputParts) {
    //        if (filename == null) {
    //          MultivaluedMap<String, String> header = inputPart.getHeaders();
    //          filename = DocumentUtils.getFileName(header);
    //          logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    //          logger.info("filename: " + filename);
    //          logger.info("header: " + header);
    //        }
    //        MediaType mt = DocumentUtils.getMediaTypeByFilename(filename);
    //        if (mt == null) {
    //          mt = inputPart.getMediaType();
    //        }
    //        InputStream inputStream = inputPart.getBody(InputStream.class, null);
    //        byte[] bytes = IOUtils.toByteArray(inputStream);
    //        inputStream.close();
    //        DocumentSimpleDto fi = new DocumentSimpleDto(filename, mt.getType() + "/" + mt.getSubtype(), (long) bytes.length);
    //        fi.setAuthorName(UserContext.getUser().getDisplayName());
    //        DocumentSimpleDto doc = documentService.store(query, bytes, fi);
    //        logger.info("Upload von " + doc.getName() + ", auf: " + doc.getPath());
    //        uploadPaths.add(doc);
    //      }
    //    } else {
    //      throw new InvalidRequestException(Status.BAD_REQUEST, "file not found in form");
    //    }
    return Response.ok(uploadPaths).status(200).build();
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.rest.document.FolderRestResource#getDocument(java.lang.Long)
   */
  @Override
  public DocumentRestResource getDocument(String documentGuid) {
    logger.info("Requesting document: " + documentGuid);
    return CDI.current().select(DocumentRestResourceImpl.class).get().with(query, documentGuid);
  }

  public FolderRestResourceImpl with(DocuQueryDto query) {
    this.query = query;
    return this;
  }

  private int compareFileDates(DocumentSimpleDto f1, DocumentSimpleDto f2) {
    FastDateFormat fdf = FastDateFormat.getInstance("dd.MM.yyyy HH:mm");
    try {
      return fdf.parse(f2.getLastModified()).compareTo(fdf.parse(f1.getLastModified()));
    } catch (ParseException ex) {
      logger.warn("can not parse date of file " + f1 + " or " + f2);
      return 0;
    }
  }

}
