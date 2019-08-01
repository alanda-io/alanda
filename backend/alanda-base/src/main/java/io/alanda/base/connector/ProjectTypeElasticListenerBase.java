package io.alanda.base.connector;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.InternalContactDto;
import io.alanda.base.dto.PmcProjectCompactDto;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.entity.PmcProject;
import io.alanda.base.util.DozerMapper;

/**
 * @author developer, FSA
 */
public abstract class ProjectTypeElasticListenerBase implements ProjectTypeElasticListener {

  private static final Logger logger = LoggerFactory.getLogger(ProjectTypeElasticListenerBase.class);

  @Inject
  private DozerMapper dozerMapper;

  @Override
  public abstract String getName();

  @Override
  public Map<String, Object> getAdditionalInfo(PmcProject project) {
    Map<String, Object> additionalInfo = new HashMap<>();
    int i = 0;
    while (project.firstParent() != null) {
      i++ ;
      if (i >= 5) {
        logger.warn("project " + project.getProjectId() + " has parents level >= " + i);
      }
      if (i >= 10) {
        logger.warn("project " + project.getProjectId() + " has parents level >= " + i + ", maxDepth reached..");
        break;
      }
      project = project.firstParent();
    }
    additionalInfo.put("rootparent", dozerMapper.map(project, PmcProjectCompactDto.class));
    return additionalInfo;
  }

  @Override
  public void filterInternalContacts(PmcProjectDto pmcProject, Map<String, InternalContactDto> ic) {}
}
