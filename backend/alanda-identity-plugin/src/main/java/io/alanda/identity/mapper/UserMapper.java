package io.alanda.identity.mapper;

import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;

import io.alanda.identity.entity.PmcUser;

public class UserMapper {

  public User map(PmcUser smUser) {
    UserEntity user = new UserEntity(smUser.getGuid().toString());
    user.setFirstName(smUser.getFirstName());
    user.setLastName(smUser.getSurname());
    user.setEmail(smUser.getEmail());
    return user;
  }
}
