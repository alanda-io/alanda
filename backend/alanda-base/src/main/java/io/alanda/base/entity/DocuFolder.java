package io.alanda.base.entity;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "PMC_DOCU_FOLDER")
public class DocuFolder {

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "NAME")
  private String name;

  @ManyToOne
  @JoinColumn(name = "PARENT_FOLDER_ID")
  private DocuFolder parentFolder;

  @OneToMany(mappedBy = "parentFolder", fetch = FetchType.EAGER)
  @OrderBy("name")
  private Collection<DocuFolder> subFolders;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DocuFolder getParentFolder() {
    return parentFolder;
  }

  public void setParentFolder(DocuFolder parentFolder) {
    this.parentFolder = parentFolder;
  }

  public Collection<DocuFolder> getSubFolders() {
    return subFolders;
  }

  public void setSubFolders(Collection<DocuFolder> subFolders) {
    this.subFolders = subFolders;
  }

  @Override
  public String toString() {
    return "DocuFolder [id=" + id + ", name=" + name + ", parentFolder=" + parentFolder + "]";
  }

}
