package io.alanda.base.service;

import java.util.Collection;

import io.alanda.base.dto.DirectoryInfoDto;
import io.alanda.base.dto.DocuConfigDto;
import io.alanda.base.dto.DocuFolderDto;
import io.alanda.base.dto.DocuQueryDto;

/**
 * @author Lennard Riebandt, lennard.riebandt@iteratec.at
 */
public interface DocuService {

  DocuConfigDto getDocuConfig(DocuQueryDto query);

  DocuFolderDto getDocuFolder(Long id);

  /**
   * @param toFolderId
   * @return a list containing the hierarchy from root to the folder
   */
  Collection<DocuFolderDto> getHierachyFromRoot(Long toFolderId);

  //List<DocuFolderDto> getHierachyFromRoot(DocuFolderDto sourceFolder, Long toFolderId);

  DirectoryInfoDto getDirectoryInfo(DocuQueryDto query);

}
