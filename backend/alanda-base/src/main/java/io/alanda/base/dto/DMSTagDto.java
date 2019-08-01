package io.alanda.base.dto;

import io.alanda.base.type.DMSTagStatus;

public class DMSTagDto {

  /**
   * identifier: In the current solution it's the abs path of the tag/node (incl. nodeName)
   */
  private String identifier;

  /**
   * tagName: Only the node name, without the path
   * e.g.: antennen
   */
  private String tagName;

  /**
   * tagAbsPathWithName: the path to the tag node, including the root node and at the end the tag node name
   * e.g:
   * tagAbsPathWithName = /tags/photo/antennen
   */
  private String tagAbsPathWithName;

  /**
   * tagRelativePathWithoutName: the path to the tag node, without the root path (including the tag's taxonomy root)
   * and without the tag/node name at the end
   * e.g:
   * tagRelativePathWithoutName = pmc/photo/
   * and the full abs path could be /tags/pmc/photo/antennen
   */
  private String tagRelativePathWithoutName;

  private String tagDisplayName = this.tagName;

  private String tagStatus = DMSTagStatus.NEW;

  private String tagNodeType;

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String uuid) {
    this.identifier = uuid;
  }

  public String getTagAbsPathWithName() {
    return tagAbsPathWithName;
  }

  public void setTagAbsPathWithName(String tagAbsPathWithName) {
    this.tagAbsPathWithName = tagAbsPathWithName;
  }

  public String getTagRelativePathWithoutName() {
    return tagRelativePathWithoutName;
  }

  public void setTagRelativePathWithoutName(String tagRelativePathWithoutName) {
    this.tagRelativePathWithoutName = tagRelativePathWithoutName;
  }

  public String getTagDisplayName() {
    return tagDisplayName;
  }

  public void setTagDisplayName(String tagDisplayName) {
    this.tagDisplayName = tagDisplayName;
  }

  @Override
  public String toString() {
    return "DMSTagDto [identifier=" +
      identifier +
      ", tagName=" +
      tagName +
      ", tagAbsPathWithName=" +
      tagAbsPathWithName +
      ", tagRelativePathWithoutName=" +
      tagRelativePathWithoutName +
      ", tagDisplayName=" +
      tagDisplayName +
      ", tagStatus=" +
      tagStatus +
      ", tagNodeType=" +
      tagNodeType +
      "]";
  }

  public String getTagStatus() {
    return tagStatus;
  }

  public void setTagStatus(String tagStatus) {
    this.tagStatus = tagStatus;
  }

  public String getTagNodeType() {
    return tagNodeType;
  }

  public void setTagNodeType(String tagNodeType) {
    this.tagNodeType = tagNodeType;
  }

  public String getTagName() {
    return tagName;
  }

  public void setTagName(String tagName) {
    this.tagName = tagName;
  }

  public boolean hasInName(String tagMask) {
    return (this.getTagDisplayName().indexOf(tagMask) > -1);
  }

}
