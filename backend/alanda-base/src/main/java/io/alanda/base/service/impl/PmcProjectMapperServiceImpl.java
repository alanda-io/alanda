package io.alanda.base.service.impl;

import io.alanda.base.connector.PmcProjectListener;
import io.alanda.base.connector.ProjectTypeElasticListener;
import io.alanda.base.dao.PmcProjectDao;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.dto.PmcProjectTypeDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.dto.ProcessDefinitionDto;
import io.alanda.base.entity.PmcProject;
import io.alanda.base.entity.PmcProjectPhase;
import io.alanda.base.entity.PmcProjectPhaseDefinition;
import io.alanda.base.service.PmcProcessService;
import io.alanda.base.service.PmcProjectService;
import io.alanda.base.service.PmcProjectService.Mode;
import io.alanda.base.util.DozerMapper;
import io.alanda.base.util.cache.UserCache;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("ProjectMapperService")
public class PmcProjectMapperServiceImpl {

  private static final Logger log = LoggerFactory.getLogger(PmcProjectMapperServiceImpl.class);

  @Inject
  private DozerMapper dozerMapper;

  @Inject
  private PmcProjectService pmcProjectService;

  @Inject
  private UserCache userCache;

  @Inject
  private PmcProjectDao pmcProjectDao;

  @Inject
  private PmcProcessService pmcProcessService;

  public PmcProjectDto mapProject(PmcProject pmcProject, Mode mode,
      Map<String, ProjectTypeElasticListener> elasticListeners) {
    boolean getRelationIds = false;

    if (mode == null) {
      mode = Mode.DEFAULT;
    }

    if (mode == Mode.RELATIONIDS) {
      mode = Mode.DEFAULT;
      getRelationIds = true;
    }

    PmcProjectDto result = dozerMapper.map(pmcProject, PmcProjectDto.class, mode.getMode());

    Set<String> activePhases = new HashSet<>();
    for (PmcProjectPhase phase : pmcProject.getPhases()) {
      if (phase.getEnabled() != null && phase.getEnabled() && phase.getActive() != null && phase.getActive()) {
        PmcProjectPhaseDefinition pmcProjectPhaseDefinition = phase.getPmcProjectPhaseDefinition();
        if(pmcProjectPhaseDefinition != null){
          activePhases.add(pmcProjectPhaseDefinition.getIdName());
        }
      }
    }
    result.setActivePhases(activePhases.toArray(new String[0]));

    if (getRelationIds) {
      mapRelationIds(pmcProject, result, elasticListeners);
    }

    mapProjectTypeDtoFields(result.getPmcProjectType());

    if (mode.equals(Mode.TREE)) {
      mapChildrenProjectTypeDtoFields(result);
      mapParentProjectTypeDtoFields(result);
    }

    PmcUserDto user = userCache.get(result.getOwnerId());
    if (user != null && user.getFirstName() != null && user.getSurname() != null) {
      result.setOwnerName(user.getFirstName() + " " + user.getSurname());
    }
    addProjectProperties(result);
    return result;
  }

  public void addProjectProperties(PmcProjectDto project) {
    for (PmcProjectListener l :
        pmcProjectService.getListener(pmcProjectDao.getById(project.getGuid()))) {
      l.getProjectProperties(project);
    }
  }

  private void mapChildrenProjectTypeDtoFields(PmcProjectDto p) {
    for (PmcProjectDto c : p.getChildren()) {
      mapProjectTypeDtoFields(c.getPmcProjectType());
      mapChildrenProjectTypeDtoFields(c);
    }
  }

  private void mapParentProjectTypeDtoFields(PmcProjectDto p) {
    for (PmcProjectDto parent : p.getParents()) {
      mapProjectTypeDtoFields(parent.getPmcProjectType());
      mapParentProjectTypeDtoFields(parent);
    }
  }

  public void mapRelationIds(PmcProject pmcProject, PmcProjectDto dto,
      Map<String, ProjectTypeElasticListener> listeners) {
    if (pmcProject.getParents() != null) {
      dto.setParentIds(new ArrayList<Long>());
      for (PmcProject parent : pmcProject.getParents()) {
        dto.getParentIds().add(parent.getGuid());
      }
    }
    if (pmcProject.getChildren() != null) {
      dto.setChildrenIds(new ArrayList<Long>());
      for (PmcProject child : pmcProject.getChildren()) {
        dto.getChildrenIds().add(child.getGuid());
      }
    }
    ProjectTypeElasticListener listener = listeners.get(pmcProject.getPmcProjectType().getIdName());
    if (listener != null) {
      dto.setAdditionalInfo(listener.getAdditionalInfo(pmcProject));
    }
  }

  public void mapProjectTypeDtoFields(PmcProjectTypeDto dto) {

    try {
      dto.setReadRightGroups(parseStringToStringList(dto.getReadRights()));
    } catch (Exception ex) {
      log.warn("PmcProjectTypeDto #{} -- invalid readRights definition: {}", dto.getGuid(), dto.getReadRights());
    }
    try {
      dto.setWriteRightGroups(parseStringToStringList(dto.getWriteRights()));
    } catch (Exception ex) {
      log.warn("PmcProjectTypeDto #{} -- invalid writeRights definition: {}", dto.getGuid(), dto.getWriteRights());
    }
    try {
      dto.setCreateRightGroups(parseStringToStringList(dto.getCreateRights()));
    } catch (Exception ex) {
      log.warn("PmcProjectTypeDto #{} -- invalid createRights definition: {}", dto.getGuid(), dto.getCreateRights());
    }
    try {
      dto.setDeleteRightGroups(parseStringToStringList(dto.getDeleteRights()));
    } catch (Exception ex) {
      log.warn("PmcProjectTypeDto #{} -- invalid deleteRights definition: {}", dto.getGuid(), dto.getDeleteRights());
    }
    dto.setAllowedTagList(parseStringToStringList(dto.getAllowedTags()));
    dto.setAllowedSubtypeList(parseStringToStringList(dto.getAllowedSubtypes()));

    List<String> procDefKeys = parseStringToStringList(dto.getAllowedProcesses());
    List<ProcessDefinitionDto> procDefDtoList = new ArrayList<>(procDefKeys.size());
    for (String procDefKey : procDefKeys) {
      ProcessDefinitionDto pd = pmcProcessService.getProcessDefinition(procDefKey);
      if (pd == null) {
        continue;
      }
      procDefDtoList.add(pd);
    }
    dto.setProcessDefinitions(procDefDtoList);
  }

  public List<String> parseStringToStringList(String input) {
    if (StringUtils.isEmpty(input)) {
      return Collections.emptyList();
    }
    String[] splitted = input.split(",");
    splitted = StringUtils.stripAll(splitted);
    return Arrays.asList(splitted);

  }
}
