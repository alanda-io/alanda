package io.alanda.base.mapper;

import io.alanda.base.dto.DocuFolderDto;
import io.alanda.base.entity.DocuFolder;

public class DocuFolderMapper extends AbstractManualMapper<DocuFolder, DocuFolderDto> {

  @Override
  public DocuFolder mapEntityFromDto(DocuFolderDto dto) {
    DocuFolder entity = null;
    if (dto != null) {
      entity = new DocuFolder();
      entity.setId(dto.getId());
      entity.setName(dto.getName());
      entity.setSubFolders(this.mapCollectionFromDto(dto.getSubFolders()));
    }
    return entity;
  }

  @Override
  public DocuFolderDto mapEntityToDto(DocuFolder entity) {
    DocuFolderDto dto = null;
    if (entity != null) {
      dto = new DocuFolderDto();
      dto.setId(entity.getId());
      dto.setName(entity.getName());
      dto.setSubFolders(this.mapCollectionToDto(entity.getSubFolders()));
      for (DocuFolderDto sub : dto.getSubFolders()) {
        //System.out.println("Set parent: dto:"+dto.getName()+" sub: "+sub.getName());
        sub.setParent(dto);
      }
    }
    return dto;
  }

}
