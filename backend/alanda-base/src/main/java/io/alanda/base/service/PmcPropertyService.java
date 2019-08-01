package io.alanda.base.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import io.alanda.base.dto.PmcPropertyDto;

public interface PmcPropertyService {

  enum PmcPropertyType {
      STRING,
      INTEGER,
      DATE,
      BOOLEAN,
      LONG
  }

  String dateFormat = "dd.MM.yy HH:mm";

  Object getProperty(Long entityId, String entityType, Long pmcProjectGuid, String key);

  String getStringProperty(Long entityId, String entityType, Long pmcProjectGuid, String key);

  Integer getIntegerProperty(Long entityId, String entityType, Long pmcProjectGuid, String key);

  Date getDateProperty(Long entityId, String entityType, Long pmcProjectGuid, String key);

  Boolean getBooleanProperty(Long entityId, String entityType, Long pmcProjectGuid, String key);

  Long getLongProperty(Long entityId, String entityType, Long pmcProjectGuid, String key);

  void setProperty(Long entityId, String entityType, Long pmcProjectGuid, String key, Object value, PmcPropertyType valueType);

  void setProperty(Long entityId, String entityType, Long pmcProjectGuid, String key, Object value, PmcPropertyType valueType, boolean skipListener);

  /**
   * @deprecated use setProperty with ValueType, or the typesafe getters/setters Sets a String Property in the
   *             PropertyStore
   * @param entityId
   * @param entityType
   * @param pmcProjectGuid
   * @param key
   * @param value
   */
  @Deprecated
  void setProperty(Long entityId, String entityType, Long pmcProjectGuid, String key, Object value);

  Boolean getBooleanProperty(Long entityId, String entityType, Long pmcProjectGuid, String key, Boolean defaultValue);

  List<PmcPropertyDto> searchProperties(Long entityId, String entityType, Long pmcProjectGuid, String keyLike);

  Map<String, Object> getPropertiesMapForProject(Long pmcProjectGuid);

  List<PmcPropertyDto> getPropertiesForProject(Long pmcProjectGuid);

  List<PmcPropertyDto> getPropertiesForProjectWithPrefix(Long pmcProjectGuid, String keyPrefix, String delim);

  void setBooleanProperty(Long entityId, String entityType, Long pmcProjectGuid, String key, Boolean value);

  int deleteProperty(Long entityId, String entityType, Long pmcProjectGuid, String key);

  int deletePropertyLike(Long entityId, String entityType, Long pmcProjectGuid, String keyLike);

  void setStringProperty(Long entityId, String entityType, Long pmcProjectGuid, String key, String value);

  void setStringProperty(Long entityId, String entityType, Long pmcProjectGuid, String key, String value, boolean skipListener);

  List<PmcPropertyDto> getPropertiesWithValue(
      Long entityId,
      String entityType,
      Long pmcProjectGuid,
      String keyLike,
      Object value,
      String valueType);
}
