/**
 * 
 */
package io.alanda.base.service.cdi;

import javax.enterprise.context.RequestScoped;

import org.elasticsearch.action.update.UpdateRequestBuilder;

/**
 * @author jlo
 */
@RequestScoped
public class ElasticUpdate {

  private String id;

  private UpdateRequestBuilder urb;

  public ElasticUpdate(String id, UpdateRequestBuilder urb) {
    super();
    this.id = id;
    this.urb = urb;
  }

  public ElasticUpdate() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public UpdateRequestBuilder getUrb() {
    return urb;
  }



  public void setUrb(UpdateRequestBuilder urb) {
    this.urb = urb;
  }

  @Override
  public String toString() {
    return "ElasticUpdate [" + (id != null ? "id=" + id : "") + "]";
  }

}
