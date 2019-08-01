package io.alanda.base.service;

import java.util.Collection;

import io.alanda.base.connector.PmcProjectListener;

public interface PmcListenerService {

  Collection<PmcProjectListener> getListenerForProject(Long guid);
}
