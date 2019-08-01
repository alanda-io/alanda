/**
 * 
 */
package io.alanda.base.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author developer
 */
public class DocuQueryDto {

  public Long projectId;

  public Long projectTypeId;

  public DocuConfigDto docuConfig;

  public String procDefKey;

  public String refObjectType;

  public Long refObjectId;

  public String mappingName;

  public Long mappingId;

  public boolean fileCount;

  public Long targetFolderId;

  public Map<String, Object> paramMap = new HashMap<>();

  public String targetFolderName;

  public String displayName;

  public String primaryMappingName;

  public static List<DocuQueryDto> listForPmcProject(PmcProjectDto project, boolean fileCount) {
    List<DocuQueryDto> retVal = new ArrayList<>();
    for (DocuConfigDto doc : project.getPmcProjectType().getDocuConfigs()) {
      DocuQueryDto dto = forPmcProject(project, fileCount);
      dto.mappingName = doc.getMappingName();
      dto.docuConfig = doc;
      dto.displayName = doc.getDisplayName();
      retVal.add(dto);
    }
    return retVal;
  }

  public static DocuQueryDto getForComponent(
      String businessKey,
      Long pmcProjectGuid,
      String refObjectType,
      Long refObjectId,
      Long projectId,
      String mappingName,
      boolean fileCount) {
    DocuQueryDto dto = new DocuQueryDto();
    dto.refObjectType = refObjectType;
    dto.refObjectId = refObjectId;
    dto.projectId = projectId;
    dto.mappingName = mappingName;
    dto.fileCount = fileCount;
    return dto.withParams(refObjectId, projectId, businessKey, pmcProjectGuid);
  }

  public static DocuQueryDto getForProcess(
      String businessKey,
      Long pmcProjectGuid,
      String procDefKey,
      String refObjectType,
      Long refObjectId,
      Long projectId,
      Long projectTypeId,
      boolean fileCount) {
    DocuQueryDto dto = new DocuQueryDto();
    dto.procDefKey = procDefKey;
    dto.refObjectType = refObjectType;
    dto.refObjectId = refObjectId;

    dto.projectId = projectId;
    dto.projectTypeId = projectTypeId;
    dto.fileCount = fileCount;
    return dto.withParams(refObjectId, projectId, businessKey, pmcProjectGuid);
  }

  public static DocuQueryDto forRefObject(String businessKey, String refObjectType, Long refObjectId, boolean fileCount) {
    DocuQueryDto dto = new DocuQueryDto();
    dto.refObjectType = refObjectType;
    dto.refObjectId = refObjectId;

    dto.mappingName = refObjectType;
    dto.mappingId = refObjectId;

    dto.fileCount = fileCount;
    dto.paramMap.put("refObjectType", refObjectType);
    return dto.withParams(refObjectId, null, businessKey, null);
  }

  public static DocuQueryDto forPmcProject(PmcProjectDto project, boolean fileCount) {
    DocuQueryDto dto = new DocuQueryDto();
    dto.refObjectId = project.getRefObjectId();
    dto.refObjectType = project.getRefObjectType();
    dto.projectId = project.getCustomerProjectId();
    dto.fileCount = fileCount;

    return dto.withParams(dto.refObjectId, dto.projectId, project.getRefObjectIdName(), project.guid);
  }

  public boolean hasMapping(String mappingName) {
    return this.mappingName != null && this.mappingName.equals(mappingName);

  }

  public DocuQueryDto withParams(Long refObjectId, Long projectId, String businessKey, Long pmcProjectGuid) {
    paramMap.put("refObjectId", refObjectId);
    paramMap.put("siteId", refObjectId);
    paramMap.put("projectId", projectId);
    paramMap.put("pmcProjectGuid", pmcProjectGuid);
    paramMap.put("refObjectIdName", businessKey);
    paramMap.put("banfId", businessKey);

    return this;
  }

  public DocuQueryDto withTargetFolderId(Long targetFolderId) {
    this.targetFolderName = null;
    this.targetFolderId = targetFolderId;
    return this;
  }

  public DocuQueryDto withTargetFolderName(String targetFolderName) {
    this.targetFolderId = null;
    this.targetFolderName = targetFolderName;
    return this;
  }

  @Override
  public String toString() {
    return "DocuQueryDto [projectId=" +
      projectId +
      ", projectTypeId=" +
      projectTypeId +
      ", docuConfig=" +
      docuConfig +
      ", procDefKey=" +
      procDefKey +
      ", refObjectType=" +
      refObjectType +
      ", refObjectId=" +
      refObjectId +
      ", mappingName=" +
      mappingName +
      ", mappingId=" +
      mappingId +
      ", fileCount=" +
      fileCount +
      ", targetFolderId=" +
      targetFolderId +
      ", paramMap=" +
      paramMap +
      "]";
  }

  public DocuQueryDto withMappingName(String mappingName) {
    this.mappingName = mappingName;
    return this;
  }

}
