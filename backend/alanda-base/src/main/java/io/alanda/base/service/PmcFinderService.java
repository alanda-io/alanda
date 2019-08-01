package io.alanda.base.service;

import java.util.List;
import java.util.Map;

public interface PmcFinderService {

  List<Map<String, Object>> getFinderGridSearchResult(String searchTerm, Boolean onlyActive);

  List<Map<String, Object>> getPIOActivitiesResult(String pid, boolean extendedView);

}
