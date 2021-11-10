package io.alanda.base.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.alanda.base.dao.PmcProjectDao;
import io.alanda.base.dto.PmcProjectDto;
import io.alanda.base.entity.PmcProject;
import io.alanda.base.security.PmcShiroAuthorizationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PmcProjectServiceImplTest {

  private static final long GUID = 129L;
  private static final long DEBOUNCE_TIME = 21000L;

  @Mock
  private ElasticServiceImpl elasticService;

  @Mock
  private PmcProjectDao projectDao;

  @Mock
  private PmcProject pmcProject;

  @Mock
  private PmcProjectMapperServiceImpl mapperService;

  @Mock
  private PmcShiroAuthorizationService authorizationService;

  @Mock(answer = Answers.CALLS_REAL_METHODS)
  private ThreadSleepServiceImpl threadSleepService;

  @InjectMocks
  private PmcProjectServiceImpl pmcProjectService;

  @Test
  public void syncProjectShouldOnlyRunTwice() {
    PmcProjectDto pmcProjectDto = new PmcProjectDto();
    when(projectDao.getById(GUID)).thenReturn(pmcProject);
    when(mapperService.mapProject(any(), any(), any())).thenReturn(pmcProjectDto);
    when(authorizationService.addOrUpdateBaseAuthKeyForProject(pmcProjectDto)).thenReturn("");

    pmcProjectService.synchProject(GUID);
    pmcProjectService.synchProject(GUID);
    pmcProjectService.synchProject(GUID);
    pmcProjectService.synchProject(GUID);
    pmcProjectService.synchProject(GUID);

    threadSleepService.sleep(DEBOUNCE_TIME);
    Mockito.verify(elasticService, Mockito.times(2)).updateEntry(pmcProjectDto, true);
  }

  @Test
  public void syncProjectShouldRunOnce() throws InterruptedException {
    PmcProjectDto pmcProjectDto = new PmcProjectDto();
    when(projectDao.getById(GUID)).thenReturn(pmcProject);
    when(mapperService.mapProject(any(), any(), any())).thenReturn(pmcProjectDto);
    when(authorizationService.addOrUpdateBaseAuthKeyForProject(pmcProjectDto)).thenReturn("");

    pmcProjectService.synchProject(GUID);

    threadSleepService.sleep(DEBOUNCE_TIME);
    Mockito.verify(elasticService, Mockito.times(1)).updateEntry(pmcProjectDto, true);
  }

  @Test
  public void syncProjectShouldRunThreeTimes() throws InterruptedException {
    PmcProjectDto pmcProjectDto = new PmcProjectDto();
    when(projectDao.getById(GUID)).thenReturn(pmcProject);
    when(mapperService.mapProject(any(), any(), any())).thenReturn(pmcProjectDto);
    when(authorizationService.addOrUpdateBaseAuthKeyForProject(pmcProjectDto)).thenReturn("");

    pmcProjectService.synchProject(GUID);
    pmcProjectService.synchProject(GUID);
    threadSleepService.sleep(DEBOUNCE_TIME);
    pmcProjectService.synchProject(GUID);
    threadSleepService.sleep(DEBOUNCE_TIME);

    Mockito.verify(elasticService, Mockito.times(3)).updateEntry(pmcProjectDto, true);
  }
}