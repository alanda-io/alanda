package io.alanda.base.document;

import java.util.List;

import io.alanda.base.dto.DocuQueryDto;
import io.alanda.base.dto.PmcProjectDto;

public interface DocumentMappingResolver {

  String[] getMappingNames();

  List<DocuQueryDto> getForProject(PmcProjectDto project, String mappingName);

  List<DocuQueryDto> getForProcess(String pid, String mappingName);

  List<DocuQueryDto> getForRefObject(String refObjectType, Long refObjectId, String mappingName);
}
