package io.alanda.base.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@MappedSuperclass
public abstract class AbstractEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_sequence")
  @SequenceGenerator(name = "entity_sequence", sequenceName = "SEQ_GUID_PK", allocationSize = 1)
  private Long guid;

  @Version
  @Column(name = "VERSION")
  private Long version;

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((guid == null) ? 0 : guid.hashCode());
    result = prime * result + ((version == null) ? 0 : version.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AbstractEntity other = (AbstractEntity) obj;
    if (guid == null) {
      if (other.guid != null)
        return false;
    } else if ( !guid.equals(other.guid))
      return false;
    if (version == null) {
      if (other.version != null)
        return false;
    } else if ( !version.equals(other.version))
      return false;
    return true;
  }
  
}
