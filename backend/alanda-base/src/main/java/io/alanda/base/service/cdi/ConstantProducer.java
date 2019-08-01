package io.alanda.base.service.cdi;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ConstantProducer {
  
  @Produces
  @PersistenceContext(name = "pmcDB", unitName = "pmcDB")
  protected EntityManager em;

  @Produces
  @JBossHome
  public String getJBossHome() {
    return System.getenv("JBOSS_HOME");
  }

}
