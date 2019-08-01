package io.alanda.identity;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.GroupQuery;
import org.camunda.bpm.engine.identity.NativeUserQuery;
import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.identity.TenantQuery;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.identity.UserQuery;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.identity.ReadOnlyIdentityProvider;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;

import io.alanda.identity.entity.PmcGroup;
import io.alanda.identity.entity.PmcUser;
import io.alanda.identity.mapper.GroupMapper;
import io.alanda.identity.mapper.UserMapper;
import io.alanda.identity.tablegateway.PmcGatewayFactory;
import io.alanda.identity.tablegateway.PmcGroupGateway;
import io.alanda.identity.tablegateway.PmcUserGateway;

/**
 * @author Lennard Riebandt, lennard.riebandt@iteratec.at
 */
public class PmcIdentityProviderSession implements ReadOnlyIdentityProvider {

  protected PmcConfiguration pmcConfiguration;

  protected PmcUserGateway userGateway;

  protected PmcGroupGateway groupGateway;

  protected PasswordServiceImpl passwordService;

  public PmcIdentityProviderSession(PmcConfiguration pmcConfiguration) {
    super();
    this.pmcConfiguration = pmcConfiguration;
    this.userGateway = PmcGatewayFactory.getUserGateway();
    this.groupGateway = PmcGatewayFactory.getGroupGateway();
    this.passwordService = new PasswordServiceImpl();
  }

  @Override
  public void flush() {
    // TODO Auto-generated method stub
  }

  @Override
  public void close() {
    // TODO Auto-generated method stub
    //TODO maybe use connection pooling!

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public User findUserById(String userId) {
    User user = null;

    PmcUser smUser = userGateway.findUserById(userId);
    if (smUser != null) {
      user = new UserMapper().map(smUser);
    }

    return user;
  }

  @Override
  public UserQuery createUserQuery() {
    return new PmcUserQueryImpl(Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
  }

  @Override
  public UserQuery createUserQuery(CommandContext commandContext) {
    return new PmcUserQueryImpl();
  }

  /**
   * @return always true
   */
  @Override
  public boolean checkPassword(String userId, String password) {
    //TODO is this method called when interacting with the rest api?
    PmcUser pmcUser = userGateway.findUserById(userId);
    if (pmcUser == null)
      return false;
    String credentials = userGateway.getCredentials(pmcUser.getGuid());
    try {
      return this.passwordService.checkPassword(password, credentials);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Group findGroupById(String groupId) {
    Group group = null;

    PmcGroup pmcGroup = groupGateway.findGroupById(groupId);
    if (pmcGroup != null) {
      group = new GroupMapper().map(pmcGroup);
    }

    return group;
  }

  @Override
  public GroupQuery createGroupQuery() {
    return new PmcGroupQueryImpl(Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
  }

  @Override
  public GroupQuery createGroupQuery(CommandContext commandContext) {
    return new PmcGroupQueryImpl();
  }

  public long findUserCountByQueryCriteria(PmcUserQueryImpl osirisUserQueryImpl) {
    return userGateway.findUserCountByQueryCriteria(osirisUserQueryImpl);
  }

  public List<User> findUserByQueryCriteria(PmcUserQueryImpl osirisUserQueryImpl) {
    List<User> users = new ArrayList<User>();
    UserMapper mapper = new UserMapper();
    for (PmcUser smUser : userGateway.findUserByQueryCriteria(osirisUserQueryImpl)) {
      users.add(mapper.map(smUser));
    }

    return users;
  }

  public long findGroupCountByQueryCriteria(PmcGroupQueryImpl osirisGroupQueryImpl) {
    return groupGateway.findGroupCountByQueryCriteria(osirisGroupQueryImpl);
  }

  public List<Group> findGroupByQueryCriteria(PmcGroupQueryImpl osirisGroupQueryImpl) {
    List<Group> groups = new ArrayList<Group>();

    GroupMapper mapper = new GroupMapper();
    for (PmcGroup pmcGroup : groupGateway.findGroupByQueryCriteria(osirisGroupQueryImpl)) {
      groups.add(mapper.map(pmcGroup));
    }

    return groups;
  }

  @Override
  public TenantQuery createTenantQuery() {
    return new PmcTenantQueryImpl();
  }

  @Override
  public TenantQuery createTenantQuery(CommandContext arg0) {
    return new PmcTenantQueryImpl();
  }

  @Override
  public Tenant findTenantById(String arg0) {
    // TODO multi tenancy
    return null;
  }

  @Override
  public NativeUserQuery createNativeUserQuery() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
