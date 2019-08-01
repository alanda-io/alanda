package io.alanda.identity.tablegateway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.identity.PmcUserQueryImpl;
import io.alanda.identity.entity.PmcUser;
import io.alanda.identity.jdbc.JdbcConnectionFactory;
import io.alanda.identity.jdbc.SQLHelper;

/**
 * Table gateway for table SM_PROD.SMUSER
 * 
 * @author Lennard Riebandt, lennard.riebandt@iteratec.at
 */
public enum JdbcPmcUserGateway implements PmcUserGateway {

  INSTANCE;

  private final Logger logger = LoggerFactory.getLogger(JdbcPmcUserGateway.class);

  private static final String COL_GUID = "guid";

  private static final String COL_LOGINNAME = "loginname";

  private static final String COL_FIRSTNAME = "firstname";

  private static final String COL_SURNAME = "surname";

  private static final String COL_EMAIL = "email";

  private static final String COL_MOBILE = "mobile";

  private static final String SELECT = "SELECT DISTINCT u." +
    COL_GUID +
    ", " +
    COL_LOGINNAME +
    ", " +
    COL_FIRSTNAME +
    ", " +
    COL_SURNAME +
    "," +
    COL_EMAIL +
    "," +
    COL_MOBILE +
    " FROM " +
    "pmc_user u";

  private static final String SELECT_AUTH = "SELECT credentials FROM PMC_USER WHERE guid=?";

  private static final String SELECT_COUNT = "SELECT DISTINCT count(u." + COL_GUID + ")" + " FROM " + "pmc_user u";

  /**
   * @param con don't forget to close it
   * @param userId the user's guid
   * @return
   */
  @Override
  public PmcUser findUserById(String userId) {
    try (Connection con = getConnection()) {
      String singleUserQuery = SELECT + " WHERE " + COL_GUID + " = ?";
      try (PreparedStatement statement = con.prepareStatement(singleUserQuery)) {
        statement.setLong(1, toLong(userId));
        try (ResultSet result = statement.executeQuery()) {
          if (result.next()) {
            return mapUserFromResult(result);
          }
        }
      } catch (SQLException e) {
        logger.error("Error while executing SQL-Statement: {}", singleUserQuery, e);
        SQLHelper.rollback(con);
      }
    } catch (SQLException e1) {
      logger.error("Error creating JDBC Connection", e1);
    }
    return null;
  }

  /**
   * @param con don't forget to close it
   * @param criteria
   * @return
   */
  @Override
  public List<PmcUser> findUserByQueryCriteria(PmcUserQueryImpl criteria) {
    List<PmcUser> users = new ArrayList<PmcUser>();

    try (Connection con = getConnection()) {
      int appliedFilter = 0;
      StringBuilder criteriaQuery = new StringBuilder(SELECT);
      if (StringUtils.isNotBlank(criteria.getGroupId())) {
        //join to group
        criteriaQuery
          .append(", ")
          .append("pmc_group g, ")
          .append("pmc_user_group ug where u.")
          .append(COL_GUID)
          .append("= ug.ref_user and g.guid = ug.ref_group and g.guid = ")
          .append(criteria.getGroupId());
        appliedFilter++ ;
      }

      appliedFilter = appendFilter(criteriaQuery, "u." + COL_GUID, criteria.getId(), Long.class, appliedFilter);
      appliedFilter = appendFilter(criteriaQuery, COL_FIRSTNAME, criteria.getFirstName(), String.class, appliedFilter);
      appliedFilter = appendFilter(criteriaQuery, COL_SURNAME, criteria.getLastName(), String.class, appliedFilter);
      appliedFilter = appendFilter(criteriaQuery, COL_EMAIL, criteria.getEmail(), String.class, appliedFilter);

      try (PreparedStatement statement = con.prepareStatement(criteriaQuery.toString()); ResultSet result = statement.executeQuery()) {
        while (result.next()) {
          users.add(mapUserFromResult(result));
        }
      } catch (SQLException e) {
        logger.error("Error while executing SQL-Statement: {}", criteriaQuery.toString(), e);
        SQLHelper.rollback(con);
      }
    } catch (SQLException e1) {
      logger.error("Error creating JDBC Connection", e1);
    }

    //TODO
    //    criteria.getOrderBy();
    return users;
  }

  @Override
  public long findUserCountByQueryCriteria(PmcUserQueryImpl criteria) {
    long count = 0;

    try (Connection con = getConnection()) {
      int appliedFilter = 0;
      StringBuilder criteriaQuery = new StringBuilder(SELECT_COUNT);
      if (StringUtils.isNotBlank(criteria.getGroupId())) {
        //join to group
        criteriaQuery
          .append(", ")
          .append("pmc_group g, ")
          .append("pmc_user_group ug where u.")
          .append(COL_GUID)
          .append("= ug.ref_user and g.guid = ug.ref_group and g.guid = ")
          .append(criteria.getGroupId());
        appliedFilter++ ;
      }

      appliedFilter = appendFilter(criteriaQuery, "u." + COL_GUID, criteria.getId(), Long.class, appliedFilter);
      appliedFilter = appendFilter(criteriaQuery, COL_FIRSTNAME, criteria.getFirstName(), String.class, appliedFilter);
      appliedFilter = appendFilter(criteriaQuery, COL_SURNAME, criteria.getLastName(), String.class, appliedFilter);
      appliedFilter = appendFilter(criteriaQuery, COL_EMAIL, criteria.getEmail(), String.class, appliedFilter);

      try (PreparedStatement statement = con.prepareStatement(criteriaQuery.toString()); ResultSet result = statement.executeQuery()) {
        while (result.next()) {
          count += mapCountFromResult(result);
        }
      } catch (SQLException e) {
        logger.error("Error while executing SQL-Statement: {}", criteriaQuery.toString(), e);
        SQLHelper.rollback(con);
      }
    } catch (SQLException e1) {
      logger.error("Error creating JDBC Connection", e1);
    }

    return count;
  }

  private int appendFilter(StringBuilder criteriaQuery, String col, String value, Class<?> type, int appliedFilter) {
    if (StringUtils.isNotBlank(value)) {
      appliedFilter = addPreFilter(criteriaQuery, appliedFilter);
      if (Long.class == type) {
        toLong(value);
        criteriaQuery.append(col).append(" = ").append(value);
      } else {
        criteriaQuery.append(col).append(" = '").append(value).append("'");
      }
    }
    return appliedFilter;
  }

  private Long toLong(String value) {
    try {
      return Long.valueOf(value);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(value + " couldn't be cast to Long", e);
    }
  }

  private int addPreFilter(StringBuilder criteriaQuery, int appliedFilter) {
    criteriaQuery.append(appliedFilter == 0 ? " WHERE " : " AND ");
    return ++appliedFilter;
  }

  private long mapCountFromResult(ResultSet result) throws SQLException {
    return result.getLong(1);
  }

  private PmcUser mapUserFromResult(ResultSet result) throws SQLException {
    PmcUser user;
    user = new PmcUser();
    user.setGuid(result.getLong(COL_GUID));
    user.setLoginName(result.getString(COL_LOGINNAME));
    user.setFirstName(result.getString(COL_FIRSTNAME));
    user.setSurname(result.getString(COL_SURNAME));
    user.setEmail(result.getString(COL_EMAIL));
    user.setMobile(result.getString(COL_MOBILE));
    return user;
  }

  private Connection getConnection() throws SQLException {
    return JdbcConnectionFactory.createConnection();
  }

  @Override
  public String getCredentials(long userGuid) {
    String sql = SELECT_AUTH;
    try (Connection con = getConnection()) {
      try (PreparedStatement statement = con.prepareStatement(SELECT_AUTH)) {
        statement.setLong(1, userGuid);
        try (ResultSet result = statement.executeQuery()) {
          if (result.next()) {
            return result.getString("credentials");
          }
        }
      }
    } catch (SQLException e) {
      logger.error("Error while executing SQL-Statement: {}", sql, e);
      throw new RuntimeException(e.getMessage());
    }
    return null;
  }

}
