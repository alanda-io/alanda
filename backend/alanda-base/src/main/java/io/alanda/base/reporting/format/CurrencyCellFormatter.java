package io.alanda.base.reporting.format;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import io.alanda.base.dto.reporting.ElasticEntryDto;
import io.alanda.base.reporting.Report;

public class CurrencyCellFormatter implements ReportCellFormatter {

  @Override
  public String getName() {
    return "Currency";
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
    if (data instanceof Integer) {
      Integer d = (Integer) data;
      BigDecimal bd = BigDecimal.valueOf(d).divide(BigDecimal.valueOf(100));
      NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.GERMANY);
      return nf.format(bd);
    }
    return data;
  }
}
