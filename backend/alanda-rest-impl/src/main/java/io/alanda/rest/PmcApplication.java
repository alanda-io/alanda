package io.alanda.rest;

import io.alanda.rest.impl.CheckListRestServiceImpl;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;


public class PmcApplication extends Application {
  
  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> classes = new HashSet<>();
    
    classes.add(io.alanda.rest.document.DocumentRestServiceImpl.class);
    classes.add(io.alanda.rest.impl.PmcPropertyRestServiceImpl.class);
    classes.add(io.alanda.rest.impl.ProjectRestServiceImpl.class);
    classes.add(io.alanda.rest.impl.PmcBlogRestServiceImpl.class);
    classes.add(io.alanda.rest.impl.UserRestServiceImpl.class);
    classes.add(io.alanda.rest.impl.PmcProcessRestServiceImpl.class);
    classes.add(io.alanda.rest.impl.PmcTaskRestServiceImpl.class);
    classes.add(io.alanda.rest.impl.AttachmentRestServiceImpl.class);
    classes.add(io.alanda.rest.impl.PmcProcessInfoRestServiceImpl.class);
    classes.add(io.alanda.rest.impl.PmcCommentRestServiceImpl.class);
    classes.add(io.alanda.rest.impl.PmcGroupRestServiceImpl.class);
    classes.add(io.alanda.rest.impl.PmcRoleRestServiceImpl.class);
    classes.add(io.alanda.rest.impl.PmcPermissionRestServiceImpl.class);
    classes.add(io.alanda.rest.impl.PmcHistoryLogRestServiceImpl.class);
    classes.add(io.alanda.rest.impl.PmcFinderRestServiceImpl.class);
    classes.add(io.alanda.rest.impl.PmcMilestoneRestServiceImpl.class);
    classes.add(io.alanda.rest.impl.CheckListRestServiceImpl.class);
    return classes;
  }

}
