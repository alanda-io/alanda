/**
 * 
 */
package io.alanda.base.service.cdi;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.update.UpdateRequest;

/**
 * @author jlo
 */
public class ElasticBulkUpdate {

  private List<String> ids = new ArrayList<>();

  private BulkRequest bulkRequest;

  /**
   * 
   */
  public ElasticBulkUpdate() {

  }

  public List<String> getIds() {
    return ids;
  }

  public void setIds(List<String> ids) {
    this.ids = ids;
  }

  public boolean isEmpty() {
    return this.ids.isEmpty();
  }

  public void add(String id, UpdateRequest update) {
    this.ids.add(id);
    this.bulkRequest.add(update);
  }

  @Override
  public String toString() {
    return "ElasticUpdate [" + (ids != null ? "ids=" + (ids.toString() + " (" + ids.size() + ")") : "") + "]";
  }

  public BulkRequest getBulkRequest() {
    return bulkRequest;
  }

  public void setBulkRequest(BulkRequest bulkRequest) {
    this.bulkRequest = bulkRequest;
  }

}
