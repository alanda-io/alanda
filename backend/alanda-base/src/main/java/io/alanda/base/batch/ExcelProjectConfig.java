/**
 * 
 */
package io.alanda.base.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jlo
 */
public class ExcelProjectConfig implements Serializable {
  private static final Logger log = LoggerFactory.getLogger(ExcelProjectConfig.class);

  /**
   * 
   */
  private static final long serialVersionUID = -5985644487873505774L;

  public static final String PREFIX = "project.";

  public static final String PREFIX_RESULT = "result.";

  private Integer type;

  private Integer title;

  private Integer subType;

  private Integer tag;

  private Integer prio;

  private Integer dueDate;

  private Integer refObjectIdName;

  private Integer resultMessage;

  private Integer pmcProjectId;

  private Integer comment;

  private Map<String, ExcelProcessConfig> processMap = new HashMap<>();

  private Map<String, ExcelPropertyConfig> propertyMap = new HashMap<>();

  private Map<String, ExcelRoleConfig> roleMap = new HashMap<>();

  public void add(int columnIndex, String val) {
    log.trace("Adding column #{} with value: {}", columnIndex, val);
    if (val.startsWith(PREFIX)) {
      addProject(columnIndex, val.substring(PREFIX.length()));
    } else if (val.startsWith(PREFIX_RESULT)) {
      addResult(columnIndex, val.substring(PREFIX_RESULT.length()));
    } else if (val.startsWith(ExcelProcessConfig.PREFIX)) {
      addProcess(columnIndex, val.substring(ExcelProcessConfig.PREFIX.length()));
    } else if (val.startsWith(ExcelPropertyConfig.PREFIX)) {
      addProperty(columnIndex, val.substring(ExcelPropertyConfig.PREFIX.length()));
    } else if (val.startsWith(ExcelRoleConfig.PREFIX)) {
      addRole(columnIndex, val.substring(ExcelRoleConfig.PREFIX.length()));
    }

  }

  private void addProcess(int columnIndex, String substring) {
    ExcelProcessConfig c = new ExcelProcessConfig().config(columnIndex, substring);
    ExcelProcessConfig existing = this.processMap.get(c.getName());
    if (existing != null) {
      existing.merge(c);
    } else {
      this.processMap.put(c.getName(), c);

    }

  }

  private void addProperty(int columnIndex, String substring) {
    ExcelPropertyConfig c = new ExcelPropertyConfig().config(columnIndex, substring);
    this.propertyMap.put(c.getName(), c);

  }

  private void addRole(int columnIndex, String substring) {
    ExcelRoleConfig c = new ExcelRoleConfig().config(columnIndex, substring);
    this.roleMap.put(c.getName(), c);
    
  }

  private void addProject(int columnIndex, String config) {
    if (config.equals("refObjectIdName")) {
      refObjectIdName = columnIndex;
    } else if (config.equals("type")) {
      type = columnIndex;
    } else if (config.equals("subType")) {
      subType = columnIndex;
    } else if (config.equals("tag")) {
      tag = columnIndex;
    } else if (config.equals("title")) {
      title = columnIndex;
    } else if (config.equals("prio")) {
      prio = columnIndex;
    } else if (config.equals("dueDate")) {
      dueDate = columnIndex;
    } else if (config.equals("comment")) {
      comment = columnIndex;
    } else {
      throw new IllegalArgumentException("Column #" + columnIndex + ": " + config + " unknown");
    }

  }

  private void addResult(int columnIndex, String config) {
    if (config.equals("message")) {
      resultMessage = columnIndex;
    } else if (config.equals("pmcProjectId")) {
      pmcProjectId = columnIndex;
    } else {
      throw new IllegalArgumentException("Column #" + columnIndex + ": " + config + " unknown");
    }

  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "ExcelProjectConfig [" +
      (type != null ? "type=" + type + ", " : "") +
      (title != null ? "title=" + title + ", " : "") +
      (subType != null ? "subType=" + subType + ", " : "") +
      (tag != null ? "tag=" + tag + ", " : "") +
      (prio != null ? "prio=" + prio + ", " : "") +
      (dueDate != null ? "dueDate=" + dueDate + ", " : "") +
      (refObjectIdName != null ? "refObjectIdName=" + refObjectIdName + ", " : "") +
      (resultMessage != null ? "resultMessage=" + resultMessage + ", " : "") +
      (pmcProjectId != null ? "pmcProjectId=" + pmcProjectId + ", " : "") +
      (comment != null ? "comment=" + comment + ", " : "") +
      (processMap != null ? "processMap=" + processMap + ", " : "") +
      (propertyMap != null ? "propertyMap=" + propertyMap + ", " : "") +
      (roleMap != null ? "roleMap=" + roleMap : "") +
      "]";
  }

  public Integer getTitle() {
    return title;
  }

  public void setTitle(Integer title) {
    this.title = title;
  }

  public Integer getSubType() {
    return subType;
  }

  public void setSubType(Integer subType) {
    this.subType = subType;
  }

  public Integer getTag() {
    return tag;
  }

  public void setTag(Integer tag) {
    this.tag = tag;
  }

  public Integer getPrio() {
    return prio;
  }

  public void setPrio(Integer prio) {
    this.prio = prio;
  }

  public Integer getDueDate() {
    return dueDate;
  }

  public void setDueDate(Integer dueDate) {
    this.dueDate = dueDate;
  }

  public Integer getRefObjectIdName() {
    return refObjectIdName;
  }

  public void setRefObjectIdName(Integer refObjectIdName) {
    this.refObjectIdName = refObjectIdName;
  }

  public Integer getResultMessage() {
    return resultMessage;
  }

  public void setResultMessage(Integer resultMessage) {
    this.resultMessage = resultMessage;
  }

  public Integer getPmcProjectId() {
    return pmcProjectId;
  }

  public void setPmcProjectId(Integer pmcProjectId) {
    this.pmcProjectId = pmcProjectId;
  }

  public Integer getComment() {
    return comment;
  }

  public void setComment(Integer comment) {
    this.comment = comment;
  }

  public Map<String, ExcelProcessConfig> getProcessMap() {
    return processMap;
  }

  public void setProcessMap(Map<String, ExcelProcessConfig> processMap) {
    this.processMap = processMap;
  }

  public Map<String, ExcelPropertyConfig> getPropertyMap() {
    return propertyMap;
  }

  public void setPropertyMap(Map<String, ExcelPropertyConfig> propertyMap) {
    this.propertyMap = propertyMap;
  }

  public Map<String, ExcelRoleConfig> getRoleMap() {
    return roleMap;
  }

  public void setRoleMap(Map<String, ExcelRoleConfig> roleMap) {
    this.roleMap = roleMap;
  }

}
