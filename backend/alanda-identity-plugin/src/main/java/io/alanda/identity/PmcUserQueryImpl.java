package io.alanda.identity;

import java.util.List;

import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.UserQueryImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;

/**
 * @author Lennard Riebandt, lennard.riebandt@iteratec.at
 */
public class PmcUserQueryImpl extends UserQueryImpl {

  private static final long serialVersionUID = 1L;

  public PmcUserQueryImpl() {
    super();
  }

  public PmcUserQueryImpl(CommandExecutor commandExecutor) {
    super(commandExecutor);
  }

  @Override
  public long executeCount(CommandContext commandContext) {
    checkQueryOk();
    final PmcIdentityProviderSession identityProvider = getIdentityProvider(commandContext);
    return identityProvider.findUserCountByQueryCriteria(this);
  }

  @Override
  public List<User> executeList(CommandContext commandContext, Page page) {
    final PmcIdentityProviderSession identityProvider = getIdentityProvider(commandContext);
    return identityProvider.findUserByQueryCriteria(this);
  }

  private PmcIdentityProviderSession getIdentityProvider(CommandContext commandContext) {
    return (PmcIdentityProviderSession) commandContext.getReadOnlyIdentityProvider();
  }

}
