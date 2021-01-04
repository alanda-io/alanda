package io.alanda.base.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import io.alanda.base.dto.DirectoryInfoDto;
import io.alanda.base.dto.DocuQueryDto;
import io.alanda.base.dto.DocumentHistoryDto;
import io.alanda.base.dto.DocumentSimpleDto;
import io.alanda.base.dto.PmcProjectDto;

public interface DocumentService {

  String DATE_FORMAT = "yyyy-mm-dd HH:mm";

  DirectoryInfoDto getTree(DocuQueryDto query);

  void getAll(DocuQueryDto query, OutputStream output) throws IOException;

  List<DocumentSimpleDto> getFolderContent(DocuQueryDto query);

  List<DocumentSimpleDto> extractAndStore(DocuQueryDto query, String documentId, boolean keepArchive) throws IOException;

  DocumentSimpleDto get(String documentId) throws IOException;

  default void setKeyword(String documentId, String keyword) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  default void removeKeyword(String documentId, String keyword) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  default void checkout(String documentId) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  default void cancelCheckout(String documentId) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  default List<DocumentHistoryDto> showHistory(String documentId) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  default DocumentSimpleDto getFromHistory(String documentId, String version) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  void delete(String documentId) throws IOException;

  void rename(String documentId, String newDocumentName) throws IOException;

  String convertToFileSystemPath(String path);

  //methods for document handling based on RefObjectType and Id

  DocumentSimpleDto store(DocuQueryDto query, byte[] input, DocumentSimpleDto fileInfo) throws IOException;

  // methods to resolve mappings
  default List<DocuQueryDto> resolveMappingByProject(PmcProjectDto p, String mapping) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  default List<DocuQueryDto> resolveMappingByProcess(String pid, String mapping) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  default List<DocuQueryDto> resolveMappingByRefObject(String refObjectType, Long refObjectId, String mapping) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }
}
