/**
 * 
 */
package io.alanda.base.dto;

/**
 * @author jlo
 */
public class TreeConfigDto {

  /*
   * Suchkreis-Ebene (input: ['SA','SI'])
   * (Suchkreis Id ist als refObjectId im Prozess gespeichert, Prozess hat als refObjectType SA)
   * -> [{"Suchkreis 140","SA",123L},{"Site 140A","SI",125L},{"Site 140B","SI",126L},{"Site 140C","SI",1267L}]
   * 
   * Project-Ebene (input:['PR_ACQUI','SI','SA'])
   * (SiteId ist als refObjectId im Prozess gespeichert, ReconId als projectId, Prozess hat als refObjectType SI)
   * [{"Acquisitions-Doku",'PR_ACQUI'}{"Site 140A","SI",125L},{"Suchkreis 140","SA",123L}]
   * 
   */

  private String displayName;

  private String mappingName;

  private Long refObjectId;

  private DocuFolderDto folder;

  public TreeConfigDto(String displayName, String mappingName, Long refObjectId, DocuFolderDto folder) {
    super();
    this.displayName = displayName;
    this.mappingName = mappingName;
    this.refObjectId = refObjectId;
    this.folder = folder;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getMappingName() {
    return mappingName;
  }

  public void setMappingName(String mappingName) {
    this.mappingName = mappingName;
  }

  public Long getRefObjectId() {
    return refObjectId;
  }

  public void setRefObjectId(Long refObjectId) {
    this.refObjectId = refObjectId;
  }

  public DocuFolderDto getFolder() {
    return folder;
  }

  public void setFolder(DocuFolderDto folder) {
    this.folder = folder;
  }

}
