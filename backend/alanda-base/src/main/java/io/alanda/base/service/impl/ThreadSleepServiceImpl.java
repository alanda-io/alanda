package io.alanda.base.service.impl;

import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("ThreadSleepService")
public class ThreadSleepServiceImpl {

  private static final Logger log = LoggerFactory.getLogger(ThreadSleepServiceImpl.class);

  public boolean sleep(Long timeInMillis){
    try {
      Thread.sleep(timeInMillis);
    } catch (InterruptedException e) {
      log.error("An Exception occured while trying to sleep for "+timeInMillis+"ms", e);
      return false;
    }
    return true;
  }
}
