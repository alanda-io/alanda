package io.alanda.base.type;

import java.io.IOException;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import io.alanda.base.dto.PmcProjectTypeDto;
import io.alanda.base.entity.PmcProjectType;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PmcProjectTypeConfiguration {

  private Map<String, Object> config;

  private String projectTypeIdName;

  public PmcProjectTypeConfiguration(PmcProjectType pt) {
    projectTypeIdName = pt.getIdName();
    try {
      config = loadConfiguration(pt.getConfiguration());
    } catch (IOException e) {
      throw new IllegalArgumentException("can not parse configuration of project type " + pt.getIdName(), e);
    }
  }

  public PmcProjectTypeConfiguration(PmcProjectTypeDto pt) {
    projectTypeIdName = pt.getIdName();
    try {
      config = loadConfiguration(pt.getConfiguration());
    } catch (IOException e) {
      throw new IllegalArgumentException("can not parse configuration of project type " + pt.getIdName(), e);
    }
  }

  private Map<String, Object> loadConfiguration(String configJson) throws IOException {
    if (configJson != null) {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(configJson, Map.class);
    } else {
      return new HashMap();
    }
  }


  // subProcessTagMapping
  private static final String KEY_SUBPROCESS_TAG_MAPPING = "subprocessTagMapping";

  public boolean existsSubProcessTagMapping() {
    return config.containsKey(KEY_SUBPROCESS_TAG_MAPPING);
  }

  public Map<String, String> getSubProcessTagMapping() {
    return (Map<String, String>) config.get(KEY_SUBPROCESS_TAG_MAPPING);
  }


  // sub process start stuff
  private static final String KEY_START_SUBPROCESS_MESSAGE_NAME = "startSubprocessMessageName";
  private static final String KEY_START_SUBPROCESS_VARIABLE = "startSubprocessVariable";


  public String getStartSubProcessMessageName() {
    return getConfigEntry(KEY_START_SUBPROCESS_MESSAGE_NAME, String.class);
  }


  public String getStartSubprocessVariable() {
    return getConfigEntry(KEY_START_SUBPROCESS_VARIABLE, String.class);
  }


  // other stuff

  private static final String KEY_INFORM_ON_CANCELLATION = "informOnCancellation";

  public boolean existsInformOnCancellationUser() {
    return config.containsKey(KEY_INFORM_ON_CANCELLATION);
  }

  public Collection<String> getInformOnCancellationUsers() {
    String user = getConfigEntry(KEY_INFORM_ON_CANCELLATION, String.class);

    List<String> loginnames = Arrays.asList(StringUtils.stripAll(user.split(",")));
    return loginnames;
  }

  private static final String ADD_REF_OBJECT_TO_COMMENTS = "addRefObjectToComments";

  public boolean existsAddRefObjectToComments() {
    return config.containsKey(ADD_REF_OBJECT_TO_COMMENTS);
  }

  public Boolean getAddRefObjectToComments() {
    if (!existsAddRefObjectToComments())
      return null;
    return getConfigEntry(ADD_REF_OBJECT_TO_COMMENTS, Boolean.class);
  }

  private static final String PROCESS_CUSTOM_REF_OBJECT = "processCustomRefObject";

  public Map<String, Boolean> getProcessCustomRefObject() {
    if (config.containsKey(PROCESS_CUSTOM_REF_OBJECT)) {
      return (Map<String, Boolean>)getConfigEntry(PROCESS_CUSTOM_REF_OBJECT, Map.class);
    } else {
      return null;
    }
  }

  private static final String DEPLOY_ROLE_CHANGE_TO_CHILDREN = "deployRoleChangeToChildren";

  public boolean existsDeployRoleChangeToChildren() {
    return config.containsKey(DEPLOY_ROLE_CHANGE_TO_CHILDREN);
  }

  public List<String> getDeployRoleChangeToChildrenRoles() {
    if (!existsDeployRoleChangeToChildren())
      return Collections.emptyList();
    String roleList = getConfigEntry(DEPLOY_ROLE_CHANGE_TO_CHILDREN, String.class);
    return Arrays.asList(StringUtils.stripAll(roleList.split(",")));
  }


  private static final String DEPLOY_ROLE_CHANGE_TO_PARENT = "deployRoleChangeToParent";

  public boolean existsDeployRoleChangeToParent() {
    return config.containsKey(DEPLOY_ROLE_CHANGE_TO_PARENT);
  }

  public List<String> getDeployRoleChangeToParentRoles() {
    if (!existsDeployRoleChangeToParent())
      return Collections.emptyList();
    String roleList = getConfigEntry(DEPLOY_ROLE_CHANGE_TO_PARENT, String.class);
    return Arrays.asList(StringUtils.stripAll(roleList.split(",")));
  }

  private static final String PROCESS_DEFS_TO_HIDE = "processDefsToHide";

  public boolean existsProcessDefsToHide() {
    return config.containsKey(PROCESS_DEFS_TO_HIDE);
  }

  public List<String> getProcessDefsToHide() {
    if (!existsProcessDefsToHide())
      return Collections.emptyList();
    return getConfigEntry(PROCESS_DEFS_TO_HIDE, List.class);
  }


  private <T extends Object> T getConfigEntry(String key, Class<T> type) {
    if (!config.containsKey(key))
      throw new IllegalStateException (
          "project type '" + projectTypeIdName + "' has no '" +
              key + "' config entry ");
    Object result = config.get(key);
    if (!type.isInstance(result))
      throw new IllegalStateException(
          "project type '" + projectTypeIdName + "' config entry '" +
              key + "' is not of type " + type);

    return (T) result;
  }
}
