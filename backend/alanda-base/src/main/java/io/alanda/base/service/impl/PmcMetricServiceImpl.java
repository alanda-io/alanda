package io.alanda.base.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dao.PmcMetricEntityDao;
import io.alanda.base.dao.PmcMetricPropertyDao;
import io.alanda.base.entity.PmcMetricEntity;
import io.alanda.base.entity.PmcMetricProperty;
import io.alanda.base.service.PmcMetricService;

@Singleton
@Named("PmcMetricService")
public class PmcMetricServiceImpl implements PmcMetricService {

  private final Logger logger = LoggerFactory.getLogger(PmcMetricServiceImpl.class);

  @Inject
  private PmcMetricEntityDao pmcMetricEntityDao;

  @Inject
  private PmcMetricPropertyDao pmcMetricPropertyDao;

  @Override
  public void addProcessEntry(String pid, String processKey, LocalDateTime started, LocalDateTime ended) {
    addProcessEntry(pid, processKey, started, ended, Collections.emptyMap());
  }

  @Override
  public void addProcessEntry(
    String pid, String processKey, LocalDateTime started, LocalDateTime ended, Map<String, Object> properties) {
    PmcMetricEntity entity = PmcMetricEntity.createAsProcessEntity(pid, started, ended);
    entity = pmcMetricEntityDao.create(entity);
    properties.put("process-key", processKey);
    for (String key : properties.keySet()) {
      PmcMetricProperty prop = PmcMetricProperty.createWithValue(key, properties.get(key));
      prop.setMetricEntity(entity);
      pmcMetricPropertyDao.create(prop);
    }
  }

  @Override
  public List<Map<String, Object>> getCreateAp() {
    List<PmcMetricEntity> allEntities = pmcMetricEntityDao.getProcessEntities("create-ap");
    Map<LocalDateTime, List<PmcMetricEntity>> dateMap = new HashMap<>();
    for (PmcMetricEntity e : allEntities) {
      LocalDateTime d = LocalDateTime.of(e.getEnded().getYear(), e.getEnded().getMonth(), 1, 0, 0, 0);
      if (dateMap.get(d) != null) {
        dateMap.get(d).add(e);
      } else {
        dateMap.put(d, new ArrayList<>(Arrays.asList(e)));
      }
    }
    List<Map<String, Object>> result = new ArrayList<>();
    for (LocalDateTime d : dateMap.keySet()) {
      int counter = 0;
      int iterationSum = 0;
      for (PmcMetricEntity e : dateMap.get(d)) {
        PmcMetricProperty p = e.getProperty("apIteration");
        if (p != null && p.getIntegerValue() != null) {
          counter++;
          iterationSum += p.getIntegerValue();
        }
      }
      Map<String, Object> m = new HashMap<>();
      m.put("date", d.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
      m.put("closed", counter);
      m.put("avgIterations", counter != 0 ? Float.valueOf(iterationSum) / Float.valueOf(counter) : 0);
      result.add(m);
    }
    return result;
  }
}
