package io.alanda.identity.mapper;

import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.impl.persistence.entity.GroupEntity;

import io.alanda.identity.entity.PmcGroup;

public class GroupMapper {

  public Group map(PmcGroup smGroup) {
    Group group = new GroupEntity();
    group.setId(smGroup.getGuiId().toString());
    group.setName(smGroup.getGroupName());
    group.setType(null);
    return group;
  }
}
