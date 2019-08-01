package io.alanda.base.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PMC_PROJECT_MILESTONE")
public class PmcProjectMilestone extends AbstractAuditEntity implements Serializable {

  private static final long serialVersionUID = 3811079635826357530L;

  @Column(name = "FC")
  private Date fc;

  @Column(name = "BASELINE")
  private Date baseline;

  @Column(name = "ACT")
  private Date act;

  @ManyToOne
  @JoinColumn(name = "PROJECT")
  private PmcProject project;

  @ManyToOne
  @JoinColumn(name = "MILESTONE")
  private Milestone milestone;

  public PmcProjectMilestone() {
    super();
  }

  public PmcProject getProject() {
    return project;
  }

  public void setProject(PmcProject project) {
    this.project = project;
  }

  public Milestone getMilestone() {
    return milestone;
  }

  public void setMilestone(Milestone milestone) {
    this.milestone = milestone;
  }

  public Date getFc() {
    return fc;
  }

  public void setFc(Date fc) {
    this.fc = fc;
  }

  public Date getBaseline() {
    return baseline;
  }

  public void setBaseline(Date baseline) {
    this.baseline = baseline;
  }

  public Date getAct() {
    return act;
  }

  public void setAct(Date act) {
    this.act = act;
  }

}
