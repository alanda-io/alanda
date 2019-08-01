/**
 * 
 */
package io.alanda.rest.api;

/**
 * @author jlo
 */
public abstract class AbstractProjectRestResource<T extends AbstractProjectRestResource<T>> {

  protected PmcProjectRestResourceImpl projectRestResource;

  public AbstractProjectRestResource() {

  }

  public AbstractProjectRestResource(PmcProjectRestResourceImpl projectRestResource) {
    this.projectRestResource = projectRestResource;
  }

  protected void checkPermissionsForProject(String permission) {
    projectRestResource.checkPermissionsForProject(permission);
  }

  @SuppressWarnings("unchecked")
  protected T forProject(PmcProjectRestResourceImpl projectRestResource) {
    this.projectRestResource = projectRestResource;
    return (T) this;
  }

}
