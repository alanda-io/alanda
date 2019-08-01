package io.alanda.base.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PmcMetricService {

  void addProcessEntry(String pid, String processKey, LocalDateTime started, LocalDateTime ended);

  void addProcessEntry(String pid, String processKey, LocalDateTime started, LocalDateTime ended, Map<String, Object> properties);

  List<Map<String, Object>> getCreateAp();
}
