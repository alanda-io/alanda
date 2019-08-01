package io.alanda.base.connector.impl;

import java.util.List;

import io.alanda.base.connector.PmcRefObjectConnector;
import io.alanda.base.dto.InternalContactDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.dto.RefObject;
import io.alanda.base.dto.RefObjectDto;

public class PmcRefObjectConnectorImpl implements PmcRefObjectConnector {

  @Override
  public String getRefObjectType() {
    return null;
  }

  @Override
  public RefObject getRefObjectById(Long id) {
    return null;
  }

  @Override
  public PmcUserDto getUserForRole(Long refObjectId, String roleName) {
    return null;
  }

  @Override
  public List<InternalContactDto> getContacts(Long refObjectId) {
    return null;
  }

  @Override
  public RefObjectDto getRefObjectByName(String refObjectIdName) {
    // TODO Auto-generated method stub
    return null;
  }

}
