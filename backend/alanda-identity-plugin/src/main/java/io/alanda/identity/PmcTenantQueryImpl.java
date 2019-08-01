/**
 * 
 */
package io.alanda.identity;

import java.util.Collections;
import java.util.List;

import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.TenantQueryImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;

/**
 * @author jlo
 */
public class PmcTenantQueryImpl extends TenantQueryImpl {

  /**
   * 
   */
  private static final long serialVersionUID = -8324651565475263733L;

  /**
   * 
   */
  public PmcTenantQueryImpl() {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param commandExecutor
   */
  public PmcTenantQueryImpl(CommandExecutor commandExecutor) {
    super(commandExecutor);
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see org.camunda.bpm.engine.impl.AbstractQuery#executeCount(org.camunda.bpm.engine.impl.interceptor.CommandContext)
   */
  @Override
  public long executeCount(CommandContext commandContext) {
    // TODO Auto-generated method stub
    return 0;
  }

  /* (non-Javadoc)
   * @see org.camunda.bpm.engine.impl.AbstractQuery#executeList(org.camunda.bpm.engine.impl.interceptor.CommandContext, org.camunda.bpm.engine.impl.Page)
   */
  @Override
  public List<Tenant> executeList(CommandContext commandContext, Page page) {
    // TODO Auto-generated method stub
    return Collections.emptyList();
  }

}
