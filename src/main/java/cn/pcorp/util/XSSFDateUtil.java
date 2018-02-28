package cn.pcorp.util;

import org.apache.poi.ss.usermodel.DateUtil;

import java.util.Calendar;

/**
 * Created by macAdministrator on 2017/6/1.
 */
class XSSFDateUtil extends DateUtil {
  protected static int absoluteDay(Calendar cal, boolean use1904windowing) {
    return DateUtil.absoluteDay(cal, use1904windowing);
  }

}
