package io.alanda.identity.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Factory class for acquiring a {@link Connection} to data base
 * 
 * @author Lennard Riebandt, lennard.riebandt@iteratec.at
 */
public class JdbcConnectionFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(JdbcConnectionFactory.class);

  private static final String OSIRIS_DS_JNDI = "java:jboss/datasources/ProcessEngine";

  public static final String SCHEMA_NAME = "";

  private static DataSource osirisDataSource;

  /**
   * Create a new connection to the data base
   * 
   * @return
   * @throws SQLException
   */
  public static Connection createConnection() throws SQLException {
    if (osirisDataSource == null) {
      osirisDataSource = lookupOsirisDatasource();
    }
    return osirisDataSource.getConnection();

  }

  private static DataSource lookupOsirisDatasource() {
    try {
      return (DataSource) new InitialContext().lookup(OSIRIS_DS_JNDI);
    } catch (NamingException e) {
      String msg = "Unrecovarable state! Failed to lookup Osiris datasource: " + OSIRIS_DS_JNDI;
      LOGGER.error(msg, e);
      throw new RuntimeException(msg, e);
    }
  }

}
