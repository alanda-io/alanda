package io.alanda.base.reporting.format;

import javax.inject.Inject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;
import io.alanda.base.util.cache.UserCache;

/**
 * @author developer
 */
public class UserGuidFormatter implements ReportCellFormatter {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Inject
  private UserCache userCache;

  @Override
  public String getName() {
    return "UserGuid";
  }

  @Override
  public void init() {

  }

  @Override
  public XSSFCellStyle getStyle(Report report, Cell cell) {
    return null;
  }

  @Override
  public Object formatCell(Object data, ElasticEntryDto entry, Report report) {
    if (data instanceof String) {
      Long guid = Long.parseLong((String) data);
      PmcUserDto user = userCache.get(guid);
      if (user != null) {
        return user.getFirstName() + " " + user.getSurname();
      } else {
        logger.warn("no user found for guid=" + guid);
      }
    }
    return data;
  }
}
