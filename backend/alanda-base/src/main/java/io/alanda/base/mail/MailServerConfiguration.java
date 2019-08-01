/**
 * 
 */
package io.alanda.base.mail;


/**
 * @author jlo
 */
public class MailServerConfiguration {

  public String host;

  public int port;

  public String username;

  public String password;

  private boolean ssl;

  public MailServerConfiguration(String host, int port, String username, String password, boolean ssl) {
    super();
    this.host = host;
    this.port = port;
    this.username = username;
    this.password = password;
    this.ssl = ssl;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isSsl() {
    return ssl;
  }

  public void setSsl(boolean ssl) {
    this.ssl = ssl;
  }

  @Override
  public String toString() {
    return "MailServerConfiguration [host=" +
      host +
      ", port=" +
      port +
      ", username=" +
      username +
      ", password=" +
      password.substring(0, 2) + "......." +
      ", ssl=" +
      ssl +
      "]";
  }

}
