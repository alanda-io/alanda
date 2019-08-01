package io.alanda.identity.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLHelper {

  public static void close(AutoCloseable... closeables) {
    for (AutoCloseable c : closeables) {
      if (c != null) {
        try {
          c.close();
        } catch (Exception e) {
          //ignore
        }
      }
    }
  }

  public static void rollback(Connection con) {
    try {
      if (con != null && !con.isClosed()) {
        con.rollback();
      }
    } catch (SQLException e) {
      //ignore
    }
  }
}
