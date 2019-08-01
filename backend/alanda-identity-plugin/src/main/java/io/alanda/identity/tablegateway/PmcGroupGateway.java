package io.alanda.identity.tablegateway;

import java.util.List;

import io.alanda.identity.PmcGroupQueryImpl;
import io.alanda.identity.entity.PmcGroup;

public interface PmcGroupGateway {

  PmcGroup findGroupById(String groupId);

  List<PmcGroup> findGroupByQueryCriteria(PmcGroupQueryImpl criteria);

  long findGroupCountByQueryCriteria(PmcGroupQueryImpl criteria);

}
