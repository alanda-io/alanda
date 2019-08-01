package io.alanda.base.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dao.DocuConfigMappingDao;
import io.alanda.base.dao.DocuFolderDao;
import io.alanda.base.dao.PmcProjectDao;
import io.alanda.base.dto.DirectoryInfoDto;
import io.alanda.base.dto.DocuConfigDto;
import io.alanda.base.dto.DocuFolderDto;
import io.alanda.base.dto.DocuQueryDto;
import io.alanda.base.entity.DocuConfigMapping;
import io.alanda.base.entity.DocuConfigProcessMapping;
import io.alanda.base.entity.DocuFolder;
import io.alanda.base.mapper.DocuConfigMapper;
import io.alanda.base.service.ConfigService;
import io.alanda.base.service.DocuService;
import io.alanda.base.service.TemplateService;
import io.alanda.base.util.DozerMapper;

@Alternative
@Priority(0)
public class DocuServiceImpl implements DocuService {

  private final Logger logger = LoggerFactory.getLogger(DocuServiceImpl.class);

  public static final String PROJECTID = "projectId";

  @Inject
  private DocuConfigMappingDao docuConfigMappingDao;

  @Inject
  private DocuFolderDao docuFolderDao;

  @Inject
  TemplateService templateService;

  @Inject
  private ConfigService configService;

  @Inject
  private DozerMapper dozerMapper;

  @Inject
  private DocuConfigMapper docuConfigMapper;

  public DocuServiceImpl() {

  }

  public DocuServiceImpl(DocuConfigMappingDao docuConfigMappingDao, DocuFolderDao docuFolderDao, PmcProjectDao pmcProjectDao) {
    this.docuConfigMappingDao = docuConfigMappingDao;
    this.docuFolderDao = docuFolderDao;
  }

  private String buildFolderPath(Collection<DocuFolderDto> hierarchy) {

    Iterator<DocuFolderDto> iter = hierarchy.iterator();
    DocuFolderDto root = iter.next();
    String rootPath = root.getName();
    if (rootPath.endsWith("/")) {
      rootPath = rootPath.substring(0, rootPath.length() - 1);
    }
    StringBuilder builder = new StringBuilder(rootPath);
    while (iter.hasNext()) {
      builder.append("/").append(iter.next().getName());
    }
    return builder.toString();
  }

  private String evaluateFolderPathTemplate(Map<String, Object> paramMap, String folderPathTemplate) {
    logger.info("Folder path template: " + folderPathTemplate);
    String folderPath = templateService.evaluateTemplate(folderPathTemplate, paramMap);
    logger.info("Folder path: " + folderPath);
    return folderPath;
  }

  private DocuFolderDto findFolder(DocuFolderDto root, String name) {
    logger.info("folder:" + root.getName() + ", searchName: " + name);
    if (root.getSubFolders() == null)
      return null;
    for (DocuFolderDto sub : root.getSubFolders()) {
      if (sub.getName().equals(name))
        return sub;
    }
    // Not found
    for (DocuFolderDto sub : root.getSubFolders()) {
      DocuFolderDto found = findFolder(sub, name);
      if (found != null)
        return found;
    }
    return null;
  }

  @Override
  public DocuConfigDto getDocuConfig(DocuQueryDto query) {
    if (query.mappingName != null) {
      return docuConfigMapper.mapEntityToDto(docuConfigMappingDao.getByMappingName(query.mappingName));
    }
    if (query.projectTypeId != null) {

      List<DocuConfigMapping> projectMappings = docuConfigMappingDao.getByProjectTypeId(query.projectTypeId);
      if (projectMappings.size() == 1) {
        //mapping is unique
        return docuConfigMapper.mapEntityToDto(projectMappings.iterator().next().getDocuConfig());
        //          return dozerMapper.map(projectMappings.iterator().next().getDocuConfig(), DocuConfigDto.class);
      } else if (projectMappings.size() > 1) {
        for (DocuConfigMapping mapping : projectMappings) {
          for (DocuConfigProcessMapping processMapping : mapping.getProcessMappings()) {
            if (processMapping.getProcessDefKey().equals(query.procDefKey)) {
              return docuConfigMapper.mapEntityToDto(mapping.getDocuConfig());
              //                return dozerMapper.map(mapping.getDocuConfig(), DocuConfigDto.class);
            }
          }
        }
        for (DocuConfigMapping mapping : projectMappings) {
          if (mapping.getProcessMappings().isEmpty()) {
            return docuConfigMapper.mapEntityToDto(mapping.getDocuConfig());
          }
        }
        throwException(query.projectId, query.procDefKey);
      }

    }
    DocuConfigMapping refObjectTypeMapping = docuConfigMappingDao.getByRefObjectType(query.refObjectType);
    return docuConfigMapper.mapEntityToDto(refObjectTypeMapping.getDocuConfig());
    //    return dozerMapper.map(refObjectTypeMapping.getDocuConfig(), DocuConfigDto.class);
  }

  @Override
  public DocuFolderDto getDocuFolder(Long id) {
    //    return new DocuFolderMapper().mapEntityToDto(docuFolderDao.getById(id));
    return dozerMapper.map(docuFolderDao.getById(id), DocuFolderDto.class);
  }

  @Override
  public Collection<DocuFolderDto> getHierachyFromRoot(Long toFolderId) {
    List<DocuFolder> hierachy = new ArrayList<>();
    DocuFolder folder = docuFolderDao.getById(toFolderId);
    hierachy.add(folder);

    while ((folder = folder.getParentFolder()) != null) {
      hierachy.add(folder);
    }

    Collections.reverse(hierachy);
    //    return new DocuFolderMapper().mapCollectionToDto(hierachy);
    return dozerMapper.mapCollection(hierachy, DocuFolderDto.class);
  }

  //@Override
  public List<DocuFolderDto> getHierachyFromRoot(DocuFolderDto sourceFolder, Long toFolderId) {

    List<DocuFolderDto> hierachy = new ArrayList<>();
    DocuFolderDto folder = findFolder(sourceFolder, toFolderId);
    hierachy.add(folder);

    while ((folder = folder.getParent()) != null) {
      hierachy.add(folder);
    }

    Collections.reverse(hierachy);
    return hierachy;
  }

  private DocuFolderDto findFolder(DocuFolderDto sourceFolder, Long toFolderId) {
    if (sourceFolder.getId().equals(toFolderId))
      return sourceFolder;
    for (DocuFolderDto child : sourceFolder.getSubFolders()) {
      DocuFolderDto root = findFolder(child, toFolderId);
      if (root != null)
        return root;
    }
    return null;
  }

  @Override
  public DirectoryInfoDto getDirectoryInfo(DocuQueryDto query) {

    DocuConfigDto cfg = this.getDocuConfig(query);

    if (query.targetFolderName != null) {
      DocuFolderDto folder = findFolder(cfg.getSourceFolder(), query.targetFolderName);
      if (folder == null) {
        throw new IllegalArgumentException(
          "No Folder named '" + query.targetFolderName + "' found in " + "docu config " + cfg.getDisplayName() + " (" + cfg.getId() + ")");
      }
      query.targetFolderId = folder.getId();
    }

    if (query.targetFolderId == null)
      query.targetFolderId = cfg.getSourceFolder().getId();
    List<DocuFolderDto> hierarchy = this.getHierachyFromRoot(cfg.getSourceFolder(), query.targetFolderId);
    String folderPathTemplate = buildFolderPath(hierarchy);
    String parsedPath = evaluateFolderPathTemplate(query.paramMap, folderPathTemplate);
    String subFolder = "/";

    int rootIndex = -1;
    int i = 0;
    for (DocuFolderDto dto : hierarchy) {
      if (dto.getId().equals(cfg.getSourceFolder().getId())) {
        rootIndex = i;
        break;
      }
      i++ ;
    }
    if (rootIndex >= 0 && hierarchy.size() > rootIndex + 1) {
      subFolder = buildFolderPath(hierarchy.subList(rootIndex + 1, hierarchy.size()));
      if ( !subFolder.startsWith("/")) {
        subFolder = "/" + subFolder;
      }
      if ( !subFolder.endsWith("/")) {
        subFolder = subFolder + "/";
      }
    }

    addSubfolderData(hierarchy.get(hierarchy.size() - 1), parsedPath);

    //TODO ConfigRoot
    String rootPath = configService.getProperty(ConfigService.DOCUMENT_ROOT_DIR);
    rootPath = rootPath + parsedPath;

    return new DirectoryInfoDto(cfg, hierarchy, parsedPath, subFolder, query.projectId, query.refObjectId, query.refObjectType);
  }

  private void addSubfolderData(DocuFolderDto docuFolderDto, String path) {
    docuFolderDto.setPath(path);
    if ( !path.endsWith("/"))
      path = path + "/";
    for (DocuFolderDto child : docuFolderDto.getSubFolders()) {
      addSubfolderData(child, path + child.getName());
    }

  }

  private void throwException(Long projectId, String procDefKey) {
    throw new IllegalArgumentException(
      "DocuConfig mapping for project '" + projectId + "' is not unique. No mapping for process '" + procDefKey + "' found");
  }

}
