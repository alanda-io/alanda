package io.alanda.base.mapper;

import javax.inject.Named;

import org.dozer.CustomConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.DocuConfigDto;
import io.alanda.base.entity.DocuConfig;

@Named
public class DocuConfigMapper extends AbstractManualMapper<DocuConfig, DocuConfigDto> implements CustomConverter {

  private static final Logger log = LoggerFactory.getLogger(DocuConfigMapper.class);

  
  private DocuFolderMapper docuFolderMapper=new DocuFolderMapper();

  @Override
  public DocuConfig mapEntityFromDto(DocuConfigDto dto) {
    DocuConfig entity = null;
    if (dto != null) {
      entity = new DocuConfig();
      entity.setId(dto.getId());
      entity.setSourceFolder(docuFolderMapper.mapEntityFromDto(dto.getSourceFolder()));
    }
    return entity;
  }

  private long[] parseString(String input) {
    String[] splitted = input.split(",");
    long[] retVal = new long[splitted.length];
    for (int i = 0; i < splitted.length; i++ ) {
      retVal[i] = Long.parseLong(splitted[i]);
    }
    return retVal;

  }

  @Override
  public DocuConfigDto mapEntityToDto(DocuConfig entity) {
   
    DocuConfigDto dto = null;
    if (entity != null) {
      //logger.info("Mapping entity: " + entity.getId() + ", sourceFolder:" + entity.getSourceFolder() + ", mapper: " + dozerMapper);
      dto = new DocuConfigDto();
      dto.setId(entity.getId());
      dto.setSourceFolder(docuFolderMapper.mapEntityToDto(entity.getSourceFolder()));
      dto.setModuleFolder(entity.getModuleFolder());
      dto.setModuleName(entity.getModuleName());
      dto.setType(entity.getType());
      dto.setSubType(entity.getSubType());
      dto.setMappingName(entity.getMappingName());
      dto.setDisplayName(entity.getDisplayName());
      if (entity.getWriteAccess() != null) {
        try {
          dto.setWriteAccess(parseString(entity.getWriteAccess()));
        } catch (Exception ex) {
          log.warn("DoCuConfig #{} -- invalid writeAccess definition: {}", entity.getId(), entity.getWriteAccess());
        }
      }
      if (entity.getReadAccess() != null) {
        try {
          dto.setReadAccess(parseString(entity.getReadAccess()));
        } catch (Exception ex) {
          log.warn("DoCuConfig #{} -- invalid readAccess definition: {}", entity.getId(), entity.getWriteAccess());
        }
      }
    } else {
      log.info("NULLEntity");
    }
    return dto;
  }

  @Override
  public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
    if (destinationClass.equals(DocuConfigDto.class)) {
      return mapEntityToDto((DocuConfig) sourceFieldValue);
    } else {
      return mapEntityFromDto((DocuConfigDto) sourceFieldValue);
    }
  }
}
