package io.alanda.identity.tablegateway;

import java.util.List;

import io.alanda.identity.PmcUserQueryImpl;
import io.alanda.identity.entity.PmcUser;

public interface PmcUserGateway {

  PmcUser findUserById(String userId);

  List<PmcUser> findUserByQueryCriteria(PmcUserQueryImpl criteria);

  long findUserCountByQueryCriteria(PmcUserQueryImpl criteria);

  String getCredentials(long userGuid);
}
