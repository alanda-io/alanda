package io.alanda.base.connector;

import java.util.Map;

import io.alanda.base.dto.InternalContactDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.entity.PmcProject;

/**
 *
 * @author developer, FSA
 */
public interface ProjectTypeElasticListener {
  
  String getName();
  
  Map<String, Object> getAdditionalInfo(PmcProject project);

  void filterInternalContacts(PmcProjectDto pmcProject, Map<String, InternalContactDto> ic);
}
