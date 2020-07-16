package io.alanda.base.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.connector.PmcProjectListener;
import io.alanda.base.dao.PmcProjectDao;
import io.alanda.base.dao.PmcPropertyDao;
import io.alanda.base.dto.PmcPropertyDto;
import io.alanda.base.entity.PmcProject;
import io.alanda.base.entity.PmcProperty;
import io.alanda.base.service.PmcListenerService;
import io.alanda.base.service.PmcPropertyService;
import io.alanda.base.util.DozerMapper;

@Stateless
public class PmcPropertyServiceImpl implements PmcPropertyService {

  private static final Logger log = LoggerFactory.getLogger(PmcPropertyServiceImpl.class);

  @Inject
  private PmcPropertyDao pmcPropertyDao;

  @Inject
  private PmcProjectDao pmcProjectDao;

  @Inject
  private DozerMapper dozerMapper;

  @Inject
  private PmcListenerService pmcListenerService;

  @Override
  public Boolean getBooleanProperty(Long entityId, String entityType, Long pmcProjectGuid, String key, Boolean defaultValue) {
    Object o = getProperty(entityId, entityType, pmcProjectGuid, key);
    if (o == null)
      return defaultValue;
    if (o instanceof Boolean) {
      return (Boolean) o;
    }
    throw new IllegalArgumentException(
      "Property " +
        key +
        " for project: " +
        pmcProjectGuid +
        ", entityId: " +
        entityId +
        ", entityType: " +
        entityType +
        ": value is no valid boolean: " +
        o);
  }

  @Override
  public Object getProperty(Long entityId, String entityType, Long pmcProjectGuid, String key) {

    PmcProperty pmcProperty = pmcPropertyDao.getProperty(key, entityId, entityType, pmcProjectGuid);

    if (pmcProperty == null)
      return null;

    //logger.info("found pmcProperty " + pmcProperty);

    return convertPropertyToType(pmcProperty);
  }

  private Object convertPropertyToType(PmcProperty pmcProperty) {
    // Type == STRING
    if (pmcProperty.getValueType().equals(PmcPropertyType.STRING.toString())) {
      return pmcProperty.getValue();
    }

    // Type == INTEGER
    if (pmcProperty.getValueType().equals(PmcPropertyType.INTEGER.toString())) {
      return Integer.valueOf(pmcProperty.getValue());
    }

    // Type == LONG
    if (pmcProperty.getValueType().equals(PmcPropertyType.LONG.toString())) {
      return Long.valueOf(pmcProperty.getValue());
    }

    // Type == BOOLEAN
    if (pmcProperty.getValueType().equals(PmcPropertyType.BOOLEAN.toString())) {
      return Boolean.valueOf(pmcProperty.getValue());
    }

    // Type == DATE
    if (pmcProperty.getValueType().equals(PmcPropertyType.DATE.toString())) {
      return convertPropertyValueToDate(pmcProperty.getValue());
    }
    // Unsupported Type
    throw new RuntimeException("Unsupported PmcPropertyType " + pmcProperty.getValueType());
  }

  private Date convertPropertyValueToDate(String value) {
    SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
    try {
      return formatter.parse(value);
    } catch (ParseException e) {
      throw new RuntimeException("Date of property could not be parsed!");
    }
  }

  @Override
  public String getStringProperty(Long entityId, String entityType, Long pmcProjectGuid, String key) {
    return (String) getProperty(entityId, entityType, pmcProjectGuid, key);
  }

  @Override
  public List<PmcPropertyDto> searchProperties(Long entityId, String entityType, Long pmcProjectGuid, String keyLike) {
    List<PmcProperty> pmcProperties = pmcPropertyDao.getPropertiesLike(keyLike, entityId, entityType, pmcProjectGuid);
    List<PmcPropertyDto> retVal = dozerMapper.mapCollection(pmcProperties, PmcPropertyDto.class);
    return retVal;
  }

  @Override
  public Integer getIntegerProperty(Long entityId, String entityType, Long pmcProjectGuid, String key) {
    return (Integer) getProperty(entityId, entityType, pmcProjectGuid, key);
  }

  @Override
  public Date getDateProperty(Long entityId, String entityType, Long pmcProjectGuid, String key) {
    Object propValue = getProperty(entityId, entityType, pmcProjectGuid, key);
    if (propValue instanceof String)
      propValue = convertPropertyValueToDate((String) propValue);
    return (Date) propValue;
  }

  @Override
  public Boolean getBooleanProperty(Long entityId, String entityType, Long pmcProjectGuid, String key) {
    Object o = getProperty(entityId, entityType, pmcProjectGuid, key);
    if (o == null || o instanceof Boolean) {
      return (Boolean) o;
    }
    throw new IllegalArgumentException(
      "Property " +
        key +
        " for project: " +
        pmcProjectGuid +
        ", entityId: " +
        entityId +
        ", entityType: " +
        entityType +
        ": value is no valid boolean: " +
        o);
  }

  @Override
  public Long getLongProperty(Long entityId, String entityType, Long pmcProjectGuid, String key) {
    return (Long) getProperty(entityId, entityType, pmcProjectGuid, key);
  }

  @Override
  public void setProperty(Long entityId, String entityType, Long pmcProjectGuid, String key, Object value, PmcPropertyType valueType) {
    setProperty(entityId, entityType, pmcProjectGuid, key, value, valueType, false);
  }

    @Override
  public void setProperty(Long entityId, String entityType, Long pmcProjectGuid, String key, Object value, PmcPropertyType valueType, boolean skipListener) {

    if (key == null)
      throw new IllegalArgumentException("Key is not set!");
    if (value == null)
      throw new IllegalArgumentException("Value is not set!");

    // STRING as default ValueType
    if (valueType == null)
      valueType = PmcPropertyService.PmcPropertyType.STRING;

    String valueString;
    if (Objects.equals(valueType, PmcPropertyType.DATE) && value instanceof Date) {
      SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
      valueString = sdf.format(value);
    } else {
      valueString = value.toString();
    }

    // check if property already exists
    PmcProperty pmcProperty = pmcPropertyDao.getProperty(key, entityId, entityType, pmcProjectGuid);

    if (pmcProperty == null) {
      pmcProperty = new PmcProperty();
      pmcProperty.setEntityId(entityId);
      pmcProperty.setEntityType(entityType);

      if (pmcProjectGuid != null) {
        PmcProject pmcProject = pmcProjectDao.getById(pmcProjectGuid);
        if (pmcProject == null) {
          throw new RuntimeException("No project with GUID " + pmcProjectGuid + " found");
        }
        pmcProperty.setPmcProject(pmcProject);
      }

      pmcProperty.setKey(key);
      pmcProperty.setValue(valueString);
      pmcProperty.setValueType(valueType.toString());
      pmcPropertyDao.create(pmcProperty);
    } else {
      if (Objects.equals(valueString, pmcProperty.getValue()) && Objects.equals(valueType.toString(), pmcProperty.getValueType())) {
        return;
      }
      pmcProperty.setValue(valueString);
      pmcProperty.setValueType(valueType.toString());

      log.info("updateing pmcProperty: {}", pmcProperty);
    }
    pmcPropertyDao.getEntityManager().flush();

    if (pmcProjectGuid != null && !skipListener) {
      for (PmcProjectListener l : pmcListenerService.getListenerForProject(pmcProjectGuid)) {
        l.afterSetProperty(pmcProjectGuid, key, value, valueType);
      }
    }
  }

  @Override
  public void setProperty(Long entityId, String entityType, Long pmcProjectGuid, String key, Object value) {
    setProperty(entityId, entityType, pmcProjectGuid, key, value, PmcPropertyService.PmcPropertyType.STRING);
  }

  @Override
  public void setStringProperty(Long entityId, String entityType, Long pmcProjectGuid, String key, String value) {
    setProperty(entityId, entityType, pmcProjectGuid, key, value, PmcPropertyService.PmcPropertyType.STRING);
  }

  @Override
  public void setStringProperty(Long entityId, String entityType, Long pmcProjectGuid, String key, String value, boolean skipListener) {
    setProperty(entityId, entityType, pmcProjectGuid, key, value, PmcPropertyService.PmcPropertyType.STRING);
  }

  @Override
  public Map<String, Object> getPropertiesMapForProject(Long pmcProjectGuid) {
    Map<String, Object> props = new HashMap<>();
    List<PmcProperty> properties = pmcPropertyDao.getPropertiesForProject(pmcProjectGuid);
    for (PmcProperty property : properties) {
      Object o = convertPropertyToType(property);
      props.put(property.getKey(), o);
    }
    return props;
  }

  @Override
  public List<PmcPropertyDto> getPropertiesForProject(Long pmcProjectGuid) {
    return dozerMapper.mapCollection(pmcPropertyDao.getPropertiesForProject(pmcProjectGuid), PmcPropertyDto.class);
  }

  @Override
  public List<PmcPropertyDto> getPropertiesForProjectWithPrefix(Long pmcProjectGuid, String keyPrefix, String delim) {
    return searchProperties(null, null, pmcProjectGuid, keyPrefix + delim + "%");
  }

  @Override
  public void setBooleanProperty(Long entityId, String entityType, Long pmcProjectGuid, String key, Boolean value) {
    setProperty(entityId, entityType, pmcProjectGuid, key, value, PmcPropertyService.PmcPropertyType.BOOLEAN);

  }

  @Override
  public int deleteProperty(Long entityId, String entityType, Long pmcProjectGuid, String key) {
    return pmcPropertyDao.deleteProperty(key, entityId, entityType, pmcProjectGuid);
  }

  @Override
  public int deletePropertyLike(Long entityId, String entityType, Long pmcProjectGuid, String keyLike) {
    return pmcPropertyDao.deletePropertyLike(keyLike, entityId, entityType, pmcProjectGuid);
  }

  @Override
  public List<PmcPropertyDto> getPropertiesWithValue(
      Long entityId,
      String entityType,
      Long pmcProjectGuid,
      String keyLike,
      Object value,
      String valueType) {
    return dozerMapper.mapCollection(
      pmcPropertyDao.getPropertiesLikeWithValue(keyLike, value.toString(), valueType, entityId, entityType, pmcProjectGuid),
      PmcPropertyDto.class);
  }
}
