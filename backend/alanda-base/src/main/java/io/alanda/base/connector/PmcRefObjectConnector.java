package io.alanda.base.connector;

import java.util.List;

import io.alanda.base.dto.InternalContactDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.dto.RefObject;

public interface PmcRefObjectConnector {

  String getRefObjectType();

  RefObject getRefObjectById(Long id);

  PmcUserDto getUserForRole(Long refObjectId, String roleName);

  List<InternalContactDto> getContacts(Long refObjectId);

  RefObject getRefObjectByName(String refObjectIdName);
  
  default List<? extends RefObject> getRefObjects() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
