package io.alanda.base.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.service.cdi.Dozer;

@Named
public class DozerMapper {

  public DozerMapper() {
    super();
  }

  public DozerMapper(Mapper mapper) {
    super();
    this.mapper = mapper;
  }

  @Inject
  @Dozer
  private Mapper mapper;

  private static final Logger log = LoggerFactory.getLogger(DozerMapper.class);

  public <D, S> List<D> mapCollection(Collection<S> sourceCollection, Class<D> targetClass) {

    //logger.info("Mapping {} objects to targetClass[{}]", sourceCollection.size(), targetClass.getName());

    List<D> targetCollection = new ArrayList<D>(sourceCollection.size());
    for (S sourceObject : sourceCollection) {
      targetCollection.add(map(sourceObject, targetClass));
    }
    return targetCollection;
  }

  public <D, S> List<D> mapCollection(Collection<S> sourceCollection, Class<D> targetClass, String mappingContext) {

    //logger.info("Mapping {} objects to targetClass[{}]", sourceCollection.size(), targetClass.getName());

    List<D> targetCollection = new ArrayList<D>(sourceCollection.size());
    for (S sourceObject : sourceCollection) {
      targetCollection.add(map(sourceObject, targetClass, mappingContext));
    }
    return targetCollection;
  }

  public <D, S> D map(Object sourceObject, Class<D> targetClass, String mappingContext) {

    if (sourceObject == null)
      return null;

    //logger.info("source[{}] - target[{}]", sourceObject.getClass().getName(), targetClass.getName());

    D targetObject = mapper.map(sourceObject, targetClass, mappingContext);
    return targetObject;
    
  }
  
  public <D, S> D map(Object sourceObject, Class<D> targetClass) {

    if (sourceObject == null)
      return null;

    //logger.info("source[{}] - target[{}]", sourceObject.getClass().getName(), targetClass.getName());

    D targetObject = mapper.map(sourceObject, targetClass);
    return targetObject;
  }
}
