package io.alanda.base.reporting.postprocess;


import java.util.List;
import java.util.Map;

import org.elasticsearch.search.SearchHit;

import io.alanda.base.dto.reporting.ElasticEntryDto;

/**
 *
 * @author developer, FSA
 */
public interface SearchHitPostProcessor {
  
  String getName();
  
  void processHit(ElasticEntryDto hit, SearchHit[] hits, Map<String, Object> reportContext);

  List<ElasticEntryDto> flattenEntries(ElasticEntryDto hit, Map<String, Object> reportContext);
}
