package io.alanda.base.document;

import java.util.List;

import javax.inject.Inject;

import io.alanda.base.dto.DocuQueryDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.service.PmcProjectService;

public class DocumentMappingResolverProjectDefault implements DocumentMappingResolver {

  @Inject
  private PmcProjectService projectService;

  @Override
  public String[] getMappingNames() {
    return new String[] {"PROJECT-DEFAULT"};
  }

  @Override
  public List<DocuQueryDto> getForProject(PmcProjectDto project, String mappingName) {
    if (project.getPmcProjectType().getDocuConfigs() == null) {
      project = projectService.getProjectByGuid(project.getGuid(), PmcProjectService.Mode.DOCU);
    }
    return DocuQueryDto.listForPmcProject(project, true);
  }

  @Override
  public List<DocuQueryDto> getForProcess(String pid, String mappingName) {
    return null;
  }

  @Override
  public List<DocuQueryDto> getForRefObject(String refObjectType, Long refObjectId, String mappingName) {
    return null;
  }
}
