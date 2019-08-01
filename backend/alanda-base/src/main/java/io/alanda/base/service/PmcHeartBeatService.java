package io.alanda.base.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dao.HeartBeatDao;
import io.alanda.base.dao.HeartBeatDefDao;
import io.alanda.base.entity.PmcHeartBeat;
import io.alanda.base.entity.PmcHeartBeatDef;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

@Transactional
@Named("heartBeatService")
public class PmcHeartBeatService {

  @Inject
  HeartBeatDefDao heartBeaDeftDao;

  @Inject
  HeartBeatDao heartBeatDao;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public void finished(DelegateExecution execution) throws Exception {
    String processKey = execution
      .getProcessInstance()
      .getProcessEngineServices()
      .getRepositoryService()
      .getProcessDefinition(execution.getProcessDefinitionId())
      .getKey();

    updateHeartbeat(processKey);
  }

  public void updateHeartbeat(String processKey) {
    CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
    CronParser parser = new CronParser(cronDefinition);
    Optional<PmcHeartBeatDef> heartBeatDefResult = heartBeaDeftDao.getHeartBeatDefByProcessKey(processKey);
    if (heartBeatDefResult.isPresent()) {
      String cronExpression = heartBeaDeftDao.getHeartBeatDefByProcessKey(processKey).get().getCronExpression();
      ExecutionTime executionTime = ExecutionTime.forCron(parser.parse(cronExpression));
      ZonedDateTime now = ZonedDateTime.now();
      ZonedDateTime nextExecution = executionTime.nextExecution(now).get();
      Timestamp tsDueDate = Timestamp.valueOf(nextExecution.toLocalDateTime());
      Timestamp tsDeadline = Timestamp
        .valueOf(
          nextExecution.toLocalDateTime().plusMinutes(heartBeatDefResult.get().getDuration() + heartBeatDefResult.get().getTolerance()));
      Timestamp tsFinishedAt = Timestamp.valueOf(LocalDateTime.now());

      Optional<PmcHeartBeat> heartBeatResult = heartBeatDao.getHeartBeatByProcessKey(processKey);
      PmcHeartBeat heartBeat;
      if (heartBeatResult.isPresent()) {
        heartBeat = heartBeatResult.get();
        heartBeat.setFinishedAt(tsFinishedAt);
        heartBeat.setDueDate(tsDueDate);
        heartBeat.setDeadline(tsDeadline);
        heartBeatDao.update(heartBeat);
      } else {
        heartBeat = new PmcHeartBeat();
        heartBeat.setName(processKey);
        heartBeat.setFinishedAt(tsFinishedAt);
        heartBeat.setDueDate(tsDueDate);
        heartBeat.setDeadline(tsDeadline);
        heartBeatDao.create(heartBeat);
      }
    } else
      throw new NotFoundException("~HeartBeatDef with processKey/id " + processKey + " not found");

  }

}
