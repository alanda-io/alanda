/**
 * 
 */
package io.alanda.rest.api;

/**
 * @author jlo
 */
public class PmcProjectRestApiImpl implements PmcProjectRestApi {

  PmcRestApiImpl restApi;

  public PmcProjectRestApiImpl(PmcRestApiImpl restApi) {
    this.restApi = restApi;
  }

  /* (non-Javadoc)
    * @see com.bpmasters.pmc.rest.api.PmcProjectRestApi#getPmcProject(java.lang.String)
    */

  @Override
  public PmcProjectRestResource getPmcProject(String projectId) {
    return new PmcProjectRestResourceImpl(projectId, restApi);
  }

}
