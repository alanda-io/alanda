package io.alanda.identity;

import java.util.List;

import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.impl.GroupQueryImpl;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;

public class PmcGroupQueryImpl extends GroupQueryImpl {

  private static final long serialVersionUID = 1L;

  public PmcGroupQueryImpl() {
    super();
  }

  public PmcGroupQueryImpl(CommandExecutor commandExecutor) {
    super(commandExecutor);
  }

  @Override
  public long executeCount(CommandContext commandContext) {
    checkQueryOk();
    PmcIdentityProviderSession identityProvider = getIdentityProvider(commandContext);
    return identityProvider.findGroupCountByQueryCriteria(this);
  }

  @Override
  public List<Group> executeList(CommandContext commandContext, Page page) {
    checkQueryOk();
    PmcIdentityProviderSession identityProvider = getIdentityProvider(commandContext);
    return identityProvider.findGroupByQueryCriteria(this);
  }

  protected PmcIdentityProviderSession getIdentityProvider(CommandContext commandContext) {
    return (PmcIdentityProviderSession) commandContext.getReadOnlyIdentityProvider();
  }

}
