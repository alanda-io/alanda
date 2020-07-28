package io.alanda.identity.tablegateway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.identity.PmcGroupQueryImpl;
import io.alanda.identity.entity.PmcGroup;
import io.alanda.identity.jdbc.JdbcConnectionFactory;
import io.alanda.identity.jdbc.SQLHelper;

/**
 * Table gateway for table SM_PROD.SMGROUP
 * 
 * @author Lennard Riebandt, lennard.riebandt@iteratec.at
 */
public enum JdbcPmcGroupGateway implements PmcGroupGateway {

  INSTANCE;

  private static final Logger log = LoggerFactory.getLogger(JdbcPmcGroupGateway.class);

  private static final String COL_GUID = "guid";

  private static final String COL_GROUPNAME = "groupname";

  private static final String SELECT = "SELECT DISTINCT g." + COL_GUID + ", " + COL_GROUPNAME + " FROM " + "pmc_group g";

  private static final String SELECT_COUNT = "SELECT DISTINCT count(g." + COL_GUID + ")" + " FROM " + "pmc_group g";

  /**
   * @param con don't forget to close it
   * @param groupId the group's guid
   * @return
   */
  @Override
  public PmcGroup findGroupById(String groupId) {
    try (Connection con = getConnection()) {
      Validate.notNull(con);
      Validate.notNull(groupId);
      Validate.notEmpty(groupId);

      isParsableLong(groupId);
      String singleGroupQuery = SELECT + " WHERE " + COL_GUID + " = " + groupId;

      try (PreparedStatement statement = con.prepareStatement(singleGroupQuery); ResultSet result = statement.executeQuery()) {
        if (result.next()) {
          return mapGroupFromResult(result);
        }
      } catch (SQLException e) {
        log.error("Error while executing SQL-Statement: {}", singleGroupQuery, e);
        SQLHelper.rollback(con);
      }
    } catch (SQLException e1) {
      log.error("Error creating JDBC Connection", e1);
    }
    return null;
  }

  /**
   * @param con don't forget to close it
   * @param criteria
   * @return
   */
  @Override
  public List<PmcGroup> findGroupByQueryCriteria(PmcGroupQueryImpl criteria) {
    List<PmcGroup> groups = new ArrayList<PmcGroup>();
    try (Connection con = getConnection()) {

      StringBuilder criteriaBuilder = new StringBuilder(SELECT);
      int appliedFilter = 0;
      appliedFilter = appendGroupFilter(criteriaBuilder, criteria.getUserId(), appliedFilter);
      appliedFilter = appendFilter(criteriaBuilder, "g." + COL_GUID, criteria.getId(), Long.class, appliedFilter);
      appliedFilter = appendFilter(criteriaBuilder, COL_GROUPNAME, criteria.getName(), String.class, appliedFilter);

      try (PreparedStatement statement = con.prepareStatement(criteriaBuilder.toString()); ResultSet result = statement.executeQuery()) {
        while (result.next()) {
          groups.add(mapGroupFromResult(result));
        }
      } catch (SQLException e) {
        log.error("Error while executing SQL-Statement: {}", criteriaBuilder.toString(), e);
      }
    } catch (SQLException e1) {
      log.error("Error creating JDBC Connection", e1);
    }
    return groups;
  }

  private int appendGroupFilter(StringBuilder criteriaBuilder, String userId, int appliedFilter) {
    if (StringUtils.isNotBlank(userId)) {
      criteriaBuilder
        .append(", ")
        .append("pmc_user u, ")
        .append("pmc_user_group ug WHERE g.")
        .append(COL_GUID)
        .append("= ug.ref_group AND u.guid = ug.ref_user AND u.guid = ")
        .append(userId);
      appliedFilter++ ;
    }
    return appliedFilter;
  }

  private int appendFilter(StringBuilder criteriaQuery, String col, String value, Class<?> type, int appliedFilter) {
    if (StringUtils.isNotBlank(value)) {
      appliedFilter = addPreFilter(criteriaQuery, appliedFilter);
      if (Long.class == type) {
        isParsableLong(value);
        criteriaQuery.append(col).append(" = ").append(value);
      } else {
        criteriaQuery.append(col).append(" = '").append(value).append("'");
      }
    }
    return appliedFilter;
  }

  private void isParsableLong(String value) {
    try {
      Long.valueOf(value);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(value + " couldn't be cast to Long", e);
    }
  }

  private int addPreFilter(StringBuilder criteriaQuery, int appliedFilter) {
    criteriaQuery.append(appliedFilter == 0 ? " WHERE " : " AND ");
    return ++appliedFilter;
  }

  private PmcGroup mapGroupFromResult(ResultSet result) throws SQLException {
    PmcGroup group;
    group = new PmcGroup();
    group.setGuiId(result.getLong(COL_GUID));
    group.setGroupName(result.getString(COL_GROUPNAME));
    return group;
  }

  private long mapCountFromResult(ResultSet result) throws SQLException {
    return result.getLong(1);
  }

  @Override
  public long findGroupCountByQueryCriteria(PmcGroupQueryImpl criteria) {
    long count = 0;
    try (Connection con = getConnection()) {

      StringBuilder criteriaBuilder = new StringBuilder(SELECT_COUNT);
      int appliedFilter = 0;
      appliedFilter = appendGroupFilter(criteriaBuilder, criteria.getUserId(), appliedFilter);
      appliedFilter = appendFilter(criteriaBuilder, "g." + COL_GUID, criteria.getId(), Long.class, appliedFilter);
      appliedFilter = appendFilter(criteriaBuilder, COL_GROUPNAME, criteria.getName(), String.class, appliedFilter);

      try (PreparedStatement statement = con.prepareStatement(criteriaBuilder.toString()); ResultSet result = statement.executeQuery()) {
        while (result.next()) {
          count += mapCountFromResult(result);
        }
      } catch (SQLException e) {
        log.error("Error while executing SQL-Statement: {}", criteriaBuilder.toString(), e);
      }
    } catch (SQLException e1) {
      log.error("Error creating JDBC Connection", e1);
    }
    return count;
  }

  private Connection getConnection() throws SQLException {
    return JdbcConnectionFactory.createConnection();
  }
}
