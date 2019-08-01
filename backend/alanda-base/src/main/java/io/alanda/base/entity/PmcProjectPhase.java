package io.alanda.base.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "PMC_PROJECT_PHASE")
public class PmcProjectPhase extends AbstractAuditEntity implements Serializable {

  @Transient
  private static final long serialVersionUID = 4366474298630539513L;

  @Transient
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @ManyToOne
  @JoinColumn(name = "PMC_PROJECT_PHASE_DEFINITION")
  private PmcProjectPhaseDefinition pmcProjectPhaseDefinition;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PMC_PROJECT")
  private PmcProject pmcProject;

  /**
   * gibt an ob die Phase in dem jeweiligen Projekt (Projektinstanz) benötigt wird. Ist eine phase enabled können
   * Prozesse angelegt werden (wenn der User berechtigt ist (prepareRights) sonst nicht.
   */
  @Column(name = "ENABLED")
  private Boolean enabled;

  /**
   * gibt an ob die Phase läuft, wird vom BPMN gesetzt
   */
  @Column(name = "ACTIVE")
  private boolean active;

  /**
   * Wann eine Phase gestartet wurde
   */
  @Column(name = "STARTDATE")
  private Date startDate;

  /**
   * Wann eine Phase beendet wurde
   */
  @Column(name = "ENDDATE")
  private Date endDate;

  @Column(name = "FROZEN")
  private boolean frozen;

  public PmcProjectPhase() {
    super();
    this.enabled = false;
    this.active = false;
  }

  public PmcProjectPhase(PmcProject p, PmcProjectPhaseDefinition def) {
    this();
    this.pmcProject = p;
    this.pmcProjectPhaseDefinition = def;
  }

  public PmcProjectPhase(PmcProject p, PmcProjectPhaseDefinition def, Boolean enabled) {
    this(p, def);
    this.enabled = enabled;
  }

  public PmcProjectPhaseDefinition getPmcProjectPhaseDefinition() {
    return pmcProjectPhaseDefinition;
  }

  public void setPmcProjectPhaseDefinition(PmcProjectPhaseDefinition pmcProjectPhaseDefinition) {
    this.pmcProjectPhaseDefinition = pmcProjectPhaseDefinition;
  }

  public PmcProject getPmcProject() {
    return pmcProject;
  }

  public void setPmcProject(PmcProject pmcProject) {
    this.pmcProject = pmcProject;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public String getEnabledAsString() {
    if (this.getEnabled() == null) {
      return "NOT SET";
    } else if (this.getEnabled().equals(true)) {
      return "ENABLED";
    } else if (this.getEnabled().equals(false)) {
      return "DISABLED";
    } else {
      return null;
    }
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public Boolean getActive() {
    return active;
  }

  public String getActiveAsString() {
    if (this.getActive() == null) {
      return "not set";
    } else if (this.getActive()) {
      return "active";
    } else if ( !this.getActive()) {
      return "deactivated";
    } else {
      return null;
    }

  }

  public void setActive(boolean active) {
    if (Boolean.TRUE.equals(active)) {
      if (this.startDate == null) {
        this.startDate = new Date();
      }
    }

    if (Boolean.FALSE.equals(active)) {
      if (Boolean.TRUE.equals(this.active)) {
        this.endDate = new Date();
      }
    }

    this.active = active;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public boolean getFrozen() {
    return frozen;
  }

  public String getFrozenAsString() {
    if (this.getFrozen()) {
      return "FROZEN";
    } else if ( !this.getFrozen()) {
      return "NOT FROZEN";
    } else {
      return null;
    }
  }

  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
  }

  @Override
  public String toString() {
    return "PmcProjectPhase [pmcProjectPhaseDefinition=" +
      pmcProjectPhaseDefinition +
      ", enabled=" +
      enabled +
      ", active=" +
      active +
      ", startDate=" +
      startDate +
      ", endDate=" +
      endDate +
      "]";
  }
}
