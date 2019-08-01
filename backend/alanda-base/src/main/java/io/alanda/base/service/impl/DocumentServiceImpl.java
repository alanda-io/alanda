package io.alanda.base.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.Priority;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.alanda.base.dao.DocumentDao;
import io.alanda.base.dto.DirectoryInfoDto;
import io.alanda.base.dto.DocuFolderDto;
import io.alanda.base.dto.DocuQueryDto;
import io.alanda.base.dto.DocumentSimpleDto;
import io.alanda.base.entity.Document;
import io.alanda.base.service.ConfigService;
import io.alanda.base.service.DocuService;
import io.alanda.base.service.DocumentService;
import io.alanda.base.service.FileService;

@Stateless
@Named("documentService")
@Alternative
@Priority(0)
public class DocumentServiceImpl implements DocumentService {

  protected static Log logger = LogFactory.getLog(DocumentServiceImpl.class);

  @Inject
  private FileService fileService;

  @Inject
  private DocuService docuService;

  @Inject
  private ConfigService configService;

  @Inject
  private DocumentDao documentDao;

  @Override
  public void getAll(DocuQueryDto query, OutputStream output) throws IOException {
    DirectoryInfoDto di = docuService.getDirectoryInfo(query);
    //    String zipFileName = buildZipFileName(di.getFolderPath());

    fileService.zipDirectory(convertToFileSystemPath(di.getFolderPath()), output);
  }

  @Override
  //TODO fix parameters
  public List<DocumentSimpleDto> getFolderContent(DocuQueryDto query) {
    // Don't move outside of method scope! SimpleDateFormat is not Threadsafe! 
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    DirectoryInfoDto di = docuService.getDirectoryInfo(query);
    Collection<Document> content = documentDao.getByPath(di.getFolderPath());
    List<DocumentSimpleDto> folderContent = new ArrayList<>();
    
    for (Document document : content) {
      folderContent.add(
        new DocumentSimpleDto(
          String.valueOf(document.getGuid()),
          document.getFileName(),
          document.getPath(),
          dateFormat.format(document.lastMod()),
          document.getMediaType(),
          document.getFileSize() / 1024));
    }
    return folderContent;
  }

  private DocumentSimpleDto storeInt(DirectoryInfoDto di, byte[] input, DocumentSimpleDto fileInfo) throws IOException {
    Document doc = documentDao.getByPathAndFilename(di.getFolderPath(), fileInfo.getName());
    boolean newFile = false;

    if (doc == null) {
      newFile = true;
      doc = new Document();
      doc.setPath(di.getFolderPath());
      doc.setFileName(fileInfo.getName());
      // not necessary, gets set in AbstractAuditEntity
      //      doc.setCreateDate(now);
      //      doc.setCreateUser(UserContext.getUser());    
    }
    doc.setMediaType(fileInfo.getMediaType());
    doc.setFileSize((long) input.length);
    String fullPath = di.getFolderPath() + "/" + fileInfo.getName();
    fileInfo.setPath(doc.getPath());
    if (newFile) {
      doc = documentDao.create(doc);
    } else {
      doc = documentDao.update(doc);
    }
    fileInfo.setGuid(String.valueOf(doc.getGuid()));
    logger.info("Attemping to write to: " + fullPath);
    fileService.writeFile(convertToFileSystemPath(fullPath), input);
    return fileInfo;
  }

  @Override
  public List<DocumentSimpleDto> extractAndStore(DocuQueryDto query, String documentId, boolean keepArchive) throws IOException {
    DirectoryInfoDto di = docuService.getDirectoryInfo(query);
    Document doc = documentDao.getById(Long.valueOf(documentId));
    Long folderId = findFolderId(doc.getPath(), di);
    List<DocumentSimpleDto> fileNames = new ArrayList<>();
    String filename = convertToFileSystemPath(doc.getPath()) + "/" + doc.getFileName();
    File f = new File(filename);

    try (ZipFile zip = new ZipFile(filename)) {
      Enumeration<? extends ZipEntry> entries = zip.entries();
      while (entries.hasMoreElements()) {
        ZipEntry ze = entries.nextElement();
        if (ze.isDirectory() || !ze.getName().toLowerCase().endsWith(".xml")) {
          continue;
        }

        DocumentSimpleDto fileInfo = new DocumentSimpleDto();
        fileInfo.setName(ze.getName());
        fileInfo.setPath(doc.getPath());
        DocumentSimpleDto ds = store(query, IOUtils.toByteArray(zip.getInputStream(ze)), fileInfo);
        fileNames.add(ds);
      }

    }
    if ( !keepArchive) {
      documentDao.delete(doc);
      boolean deleted = f.delete();
      logger.info("Deleted ZipFile " + f.getName() + ", success=" + deleted + ", extracted: " + fileNames);
    }
    return fileNames;
  }

  @Override
  public DocumentSimpleDto get(String documentId) throws IOException {
    // Don't move outside of method scope! SimpleDateFormat is not Threadsafe! 
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Document doc = documentDao.getById(Long.valueOf(documentId));
    documentDao.update(doc);
    final InputStream is = fileService.getFileInputSteam(convertToFileSystemPath(doc.getPath()) + "/" + doc.getFileName());
    DocumentSimpleDto dsd = new DocumentSimpleDto(
      String.valueOf(doc.getGuid()),
      doc.getFileName(),
      doc.getPath(),
      dateFormat.format(doc.lastMod()),
      doc.getMediaType(),
      doc.getFileSize() / 1024);
    dsd.setIntputStream(is);
    return dsd;
  }

  @Override
  public void delete(String documentId) throws IOException {
    Document doc = documentDao.getById(Long.valueOf(documentId));
    String fsPath = convertToFileSystemPath(doc.getPath() + "/" + doc.getFileName());
    logger.info("Deleting file: " + fsPath);
    fileService.deleteFile(fsPath);
    documentDao.delete(doc);
  }

  @Override
  public void rename(String documentId, String newDocumentName) throws IOException {
    Document doc = documentDao.getById(Long.valueOf(documentId));
    String fsDir = convertToFileSystemPath(doc.getPath());
    fileService.moveFile(fsDir + "/" + doc.getFileName(), fsDir + "/" + newDocumentName);

    doc.setFileName(newDocumentName);
    documentDao.update(doc);
  }

  @Override
  public String convertToFileSystemPath(String path) {
    //    if (path.startsWith("../"))
    //      path = path.substring(2);
    String rootPath = configService.getProperty(ConfigService.DOCUMENT_ROOT_DIR);
    rootPath = rootPath + path;
    return rootPath;
  }

  @Override
  public DocumentSimpleDto store(DocuQueryDto query, byte[] input, DocumentSimpleDto fileInfo) throws IOException {

    DirectoryInfoDto di = docuService.getDirectoryInfo(query);
    return storeInt(di, input, fileInfo);
  }

  private Collection<DocuFolderDto> flatten(DocuFolderDto folder) {
    Collection<DocuFolderDto> retVal = new ArrayList<>();
    retVal.add(folder);
    for (DocuFolderDto dto : folder.getSubFolders()) {
      retVal.addAll(flatten(dto));
    }
    return retVal;
  }

  private Long findFolderId(String folderName, DirectoryInfoDto directoryInfo) {
    //    folderName = removePrefix(folderName);
    for (DocuFolderDto folder : flatten(directoryInfo.getConfig().getSourceFolder())) {
      if (folderName.equals(folder.getPath())) {
        return folder.getId();
      }
    }
    return null;
  }

  @Override
  public DirectoryInfoDto getTree(DocuQueryDto query) {
    logger.info("getTree() query : " + query);
    DirectoryInfoDto dirInfo = docuService.getDirectoryInfo(query);
    DocuFolderDto folder = dirInfo.getConfig().getSourceFolder();
    logger.info("FS Root Folder: " + convertToFileSystemPath(folder.getPath()));
    if (query.fileCount) {
      for (DocuFolderDto currFolder : flatten(folder)) {
        String folderPath = currFolder.getPath();
        folderPath = convertToFileSystemPath(folderPath);
        File dir = new File(folderPath);
        if (dir.exists() && dir.isDirectory()) {
          currFolder.setFiles(fileService.listFiles(dir).length);
        } else {
          currFolder.setFiles(0);
        }
      }
    }
    return dirInfo;
  }

}
