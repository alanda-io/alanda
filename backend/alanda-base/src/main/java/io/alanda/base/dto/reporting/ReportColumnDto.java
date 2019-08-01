/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package io.alanda.base.dto.reporting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.alanda.base.reporting.ColumnType;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author developer
 */
public class ReportColumnDto {

  ColumnType type;

  String template;

  String title;

  String field;

  String bgcolor;
  
  Short fontSize;

  String formatter;

  List<ReportCallbackDto> callbacks;

  Boolean collapse;

  @JsonIgnore
  List<ReportColumnDto> path = new ArrayList<>();

  @JsonIgnore
  Integer startIndex;

  @JsonIgnore
  Integer endIndex;

  List<ReportColumnDto> columns = new ArrayList<>();

  Integer width;
  
  Map<String, Object> colStyle;

  public ReportColumnDto() {
  }

  public ReportColumnDto(ColumnType type, String title, String field) {
    this.type = type;
    this.title = title;
    this.field = field;
  }

  public ColumnType getType() {
    return type;
  }

  public void setType(ColumnType type) {
    this.type = type;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public String getTitle() {
    if (title == null)
      return "";
    else
      return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getBgcolor() {
    return bgcolor;
  }

  public void setBgcolor(String bgcolor) {
    this.bgcolor = bgcolor;
  }

  public Short getFontSize() {
    return fontSize;
  }

  public void setFontSize(Short fontSize) {
    this.fontSize = fontSize;
  }
  
  public String getFormatter() {
    return formatter;
  }

  public void setFormatter(String formatter) {
    this.formatter = formatter;
  }

  public List<ReportCallbackDto> getCallbacks() {
    return callbacks;
  }

  public void setCallbacks(List<ReportCallbackDto> callbacks) {
    this.callbacks = callbacks;
  }

  public List<ReportColumnDto> getPath() {
    return path;
  }

  public void setPath(List<ReportColumnDto> path) {
    this.path = path;
  }

  public Integer getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(Integer startIndex) {
    this.startIndex = startIndex;
  }

  public Integer getEndIndex() {
    return endIndex;
  }

  public void setEndIndex(Integer endIndex) {
    this.endIndex = endIndex;
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  @JsonIgnore
  public String[] getFieldArray() {
    return getFieldArray("\\.");
  }

  @JsonIgnore
  public String[] getFieldArray(String regex) {
    if (this.field == null) {
      return new String[0];
    } else
      return field.split(regex);
  }

  @JsonIgnore
  public Color getBgAsColor() {
    Color color;
    if (this.bgcolor == null) {
      color = new Color(0, 0, 0); // default header background is black
    } else {
      String[] rgb = bgcolor.split(",");
      try {
        color = new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
      } catch (Exception e) {
        color = new Color(255, 255, 255);
      }
    }
    return color;
  }

  public List<ReportColumnDto> getColumns() {
    return columns;
  }

  public void setColumns(List<ReportColumnDto> columns) {
    this.columns = columns;
  }

  public Boolean getCollapse() {
    if (collapse == null)
      return false;
    return collapse;
  }

  public void setCollapse(Boolean collapse) {
    this.collapse = collapse;
  }

  public Map<String, Object> getColStyle() {
    return colStyle;
  }

  public void setColStyle(Map<String, Object> colStyle) {
    this.colStyle = colStyle;
  }

  @Override
  public String toString() {
    return "ReportColumnDto [" +
      (type != null ? "type=" + type + ", " : "") +
      (template != null ? "template=" + template + ", " : "") +
      (title != null ? "title=" + title + ", " : "") +
      (field != null ? "field=" + field + ", " : "") +
      (bgcolor != null ? "bgcolor=" + bgcolor + ", " : "") +
      (formatter != null ? "formatter=" + formatter + ", " : "") +
      (collapse != null ? "collapse=" + collapse + ", " : "") +
      (startIndex != null ? "startIndex=" + startIndex + ", " : "") +
      (endIndex != null ? "endIndex=" + endIndex : "") +
      "]";
  }

  public Double getDefaultWidth() {
    if (StringUtils.equals(formatter, "DateD") || StringUtils.equals(formatter, "ToDate")) {
      return 10.43;
    } else if (StringUtils.equals(formatter, "UserGuid")) {
      return 18.43;
    } else if (StringUtils.equals(formatter, "MilestoneHistory")) {
      return 30.0;
    }
    return null;
  }

}
