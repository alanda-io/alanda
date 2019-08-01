package io.alanda.base.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "PMC_PROJECT_PHASE_DEFINITION")
public class PmcProjectPhaseDefinition extends AbstractEntity implements Serializable {

  private static final long serialVersionUID = -912125347206603549L;

  @Transient
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Column(name = "IDNAME", unique = true)
  private String idName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PMC_PROJECTTYPE")
  private PmcProjectType pmcProjectType;

  @Column(name = "DISPLAYNAME")
  private String displayName;

  /**
   * Hier sind die Prozessdefinitionen eingetragen welche gestartet werden dürfen wenn die Phase aktiv ist.
   */
  @Column(name = "ALLOWEDPROCESSES")
  private String allowedProcesses;

  /**
   * Erlaubt, dass Prozesse in der jeweilgen Phase angelegt werden können. Die angelegten Prozesse dürfen nur angelegt
   * werden, ein starten, cancellen o.ä. ist nicht möglich.
   */
  @Column(name = "PREPARERIGHTS")
  private String prepareRights;

  /**
   * Erlaubt, dass die Prozesse in der Phase definiert sind vom User gestartet werden dürfen
   */
  @Column(name = "WRITERIGHTS")
  private String writeRights;

  public PmcProjectPhaseDefinition() {
    super();
  }

  public String getIdName() {
    return idName;
  }

  public void setIdName(String idName) {
    this.idName = idName;
  }

  public PmcProjectType getPmcProjectType() {
    return pmcProjectType;
  }

  public void setPmcProjectType(PmcProjectType pmcProjectType) {
    this.pmcProjectType = pmcProjectType;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public Collection<String> getAllowedProcesses() {
    if (allowedProcesses != null) {
      return new ArrayList<>(Arrays.asList(StringUtils.stripAll(allowedProcesses.split(","), null)));
    } else {
      return Collections.emptyList();
    }
  }

  public void setAllowedProcesses(Collection<String> allowedProcesses) {
    this.allowedProcesses = joinString(allowedProcesses);
  }

  public Collection<String> getPrepareRights() {
    return splitString(prepareRights);
  }

  public void setPrepareRights(Collection<String> prepareRights) {
    this.prepareRights = joinString(prepareRights);
  }

  public Collection<String> getWriteRights() {
    return splitString(writeRights);
  }

  public void setWriteRights(Collection<String> writeRights) {
    this.writeRights = joinString(writeRights);
  }

  private Collection<String> splitString(String s) {
    if (s != null) {
      return new ArrayList<>(Arrays.asList(StringUtils.stripAll(s.split(","), null)));
    } else {
      return Collections.emptyList();
    }
  }

  private String joinString(Collection<String> c) {
    if (c != null) {
      String s = "";
      for (String sItem : c) {
        s = StringUtils.strip(s);
        s += sItem + ", ";
      }
      if (c.size() > 0) {
        s = s.substring(s.length() - 2);
      }
      return s;
    } else {
      return null;
    }
  }

  @Override
  public String toString() {
    return "PmcProjectPhaseDefinition [idName=" +
      idName +
      ", displayName=" +
      displayName +
      ", allowedProcesses=" +
      allowedProcesses +
      ", prepareRights=" +
      prepareRights +
      ", writeRights=" +
      writeRights +
      "]";
  }

}
