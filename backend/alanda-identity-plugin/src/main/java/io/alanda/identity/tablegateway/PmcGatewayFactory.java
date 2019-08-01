package io.alanda.identity.tablegateway;

public class PmcGatewayFactory {

  private PmcGatewayFactory() {
  }

  public static PmcGroupGateway getGroupGateway() {
    return JdbcPmcGroupGateway.INSTANCE;
  }

  public static PmcUserGateway getUserGateway() {
    return JdbcPmcUserGateway.INSTANCE;
  }

}
