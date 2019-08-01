/**
 *
 */
package io.alanda.base.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import io.alanda.base.dto.DMSTagDto;
import io.alanda.base.dto.DmsDocumentDto;
import io.alanda.base.dto.DocumentHistoryDto;


/**
 * @author jlo
 */
public interface DmsService {

  List<DmsDocumentDto> getFolderContent(String folderPath, String fileMask, List<String> tags);

  List<DmsDocumentDto> getFolderContent(String folderPath, List<String> tags);

  void getAll(String folderPath, List<String> fileIds, OutputStream output) throws IOException;

  List<DmsDocumentDto> extractAndStore(String folderPath, String documentId, boolean keepArchive) throws IOException;

  DmsDocumentDto get(String documentId) throws IOException;

  default List<DocumentHistoryDto> showHistory(String documentId) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  default DmsDocumentDto getFromHistory(String documentId, String version) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  void delete(String documentId) throws IOException;

  void rename(String documentId, String newDocumentName) throws IOException;

  DmsDocumentDto store(String folderPath, byte[] input, DmsDocumentDto fileInfo) throws IOException;

  DmsDocumentDto setTagsForDocument(String documentId, List<DMSTagDto> tagPathList);

  List<DmsDocumentDto> updateTagsForDocuments(List<String> documentIds, List<DMSTagDto> tagsToAdd, List<DMSTagDto> tagsToRemove);

  List<DmsDocumentDto> markAllVerified(String folderPath);

  List<DmsDocumentDto> markSomeVerified(String folderPath, List<DmsDocumentDto> documents);

  List<DmsDocumentDto> getUnverified(String folderPath);

  List<DmsDocumentDto> tag(List<DmsDocumentDto> docs);

  List<DmsDocumentDto> getUntaggedDocuments(String createPath);

}
