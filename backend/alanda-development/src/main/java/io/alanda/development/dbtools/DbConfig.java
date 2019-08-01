package io.alanda.development.dbtools;

public class DbConfig {

  public final String url;
  public final String user;
  public final String pass;
  public final boolean allowInit;
  public final boolean skipUserApproval;
  public final String label;
  public final String schema;

  public DbConfig(
      String url,
      String user,
      String pass,
      boolean allowInit,
      boolean skipUserApproval,
      String label) {
    super();
    this.url = url;
    this.user = user;
    this.pass = pass;
    this.allowInit = allowInit;
    this.skipUserApproval = skipUserApproval;
    this.label = label;
    this.schema = user;
  }

  @Override
  public String toString() {
    return "DbConfig [url="
        + url
        + ", user="
        + user
        + ", pass="
        + pass
        + ", allowInit="
        + allowInit
        + ", skipUserApproval="
        + skipUserApproval
        + ", label="
        + label
        + ", schema="
        + schema
        + "]";
  }
}
