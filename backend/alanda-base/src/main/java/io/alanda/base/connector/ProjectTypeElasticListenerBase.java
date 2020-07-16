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

  private static final Logger log = LoggerFactory.getLogger(ProjectTypeElasticListenerBase.class);

  @Inject
  private DozerMapper dozerMapper;

  @Override
  public abstract String getName();

  @Override
  public Map<String, Object> getAdditionalInfo(PmcProject project) {
    log.debug("Getting additional info for project {}", project);
    Map<String, Object> additionalInfo = new HashMap<>();
    int i = 0;
    while (project.firstParent() != null) {
      i++ ;
      if (i >= 5) {
        log.warn("project {} has parents level >= {}", project.getProjectId(), i);
      }
      if (i >= 10) {
        log.warn("project {} has parents level >= {}, maxDepth reached..", project.getProjectId(), i);
        break;
      }
      project = project.firstParent();
    }
    additionalInfo.put("rootparent", dozerMapper.map(project, PmcProjectCompactDto.class));

    log.trace("...additional info for project {}: ", additionalInfo);
    return additionalInfo;
  }

  @Override
  public void filterInternalContacts(PmcProjectDto pmcProject, Map<String, InternalContactDto> ic) {}
}
