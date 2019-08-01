package io.alanda.ejb;

import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.camunda.bpm.application.ProcessApplicationInfo;
import org.camunda.bpm.application.ProcessApplicationInterface;


/**
 * @author Daniel Meyer
 */
public class PmcServletContextListener implements ServletContextListener {
  
  @EJB
  private ProcessApplicationInterface pmcProcessApplication;

  @Override
  public void contextInitialized(ServletContextEvent contextEvent) {

    String contextPath = contextEvent.getServletContext().getContextPath();
    
    pmcProcessApplication.getProperties().put(ProcessApplicationInfo.PROP_SERVLET_CONTEXT_PATH, contextPath);
    
  }

  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
  }

}
