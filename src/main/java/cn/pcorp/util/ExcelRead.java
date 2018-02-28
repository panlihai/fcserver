package cn.pcorp.util;

/**
 * Created by macAdministrator on 2017/6/1.
 */

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 读取Excel
 * @author lp
 *
 */
public class ExcelRead {
  public int totalRows; //sheet中总行数
  public static int totalCells; //每一行总单元格数
  /**
   * read the Excel .xlsx,.xls
   * @param file jsp中的上传文件
   * @return
   * @throws IOException
   */
  public List<ArrayList<String>> readExcel(MultipartFile file) throws IOException {
    String  fileName = file.getOriginalFilename();
    System.out.print("获取文件名称："+fileName+"+++++++++++++++++++++++++++++++++++++++");
    String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
    if(file==null|| ExcelUtil.EMPTY.equals(prefix)){
      return null;
    }else{
      String postfix = ExcelUtil.getPostfix(prefix);
      if(!ExcelUtil.EMPTY.equals(prefix)){
        if(ExcelUtil.OFFICE_EXCEL_2003_POSTFIX.equals(prefix)){
          return readXls(file);
        }else if(ExcelUtil.OFFICE_EXCEL_2010_POSTFIX.equals(prefix)){
          return readXlsx(file);
        }else{
          return null;
        }
      }
    }
    return null;
  }
  /**
   * read the Excel 2010 .xlsx
   * @param file
   * @param beanclazz
   * @param titleExist
   * @return
   * @throws IOException
   */
  @SuppressWarnings("deprecation")
  public List<ArrayList<String>> readXlsx(MultipartFile file){
    List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
    // IO流读取文件
    InputStream input = null;
    Workbook wb = null;
    ArrayList<String> rowList = null;
    try {
      input = file.getInputStream();
      // 创建文档
      wb = new XSSFWorkbook(input);
      //读取sheet(页)
//      for(int numSheet=0;numSheet<wb.getNumberOfSheets();numSheet++){
      Sheet Sheet = wb.getSheetAt(0);
//        if(xssfSheet == null){
//          continue;
//        }
      totalRows = Sheet.getLastRowNum();
      //读取Row,从第二行开始
      for(int rowNum = 5;rowNum <= totalRows;rowNum++){
        Row  xssfRow = Sheet.getRow(rowNum);
        if(xssfRow!=null){
          rowList = new ArrayList<String>();
          totalCells = xssfRow.getLastCellNum();
          //读取列，从第一列开始
          for(int c=0;c<=totalCells+1;c++){
            Cell cell = xssfRow.getCell(c);
            if(cell==null){
              rowList.add(ExcelUtil.EMPTY);
              continue;
            }
            rowList.add(ExcelUtil.getHValue(cell));
          }
        }
        list.add(rowList);
//        }
      }
      return list;
    } catch (IOException e) {
      e.printStackTrace();
    } finally{
      try {
        input.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;

  }
  /**
   * read the Excel 2003-2007 .xls
   * @param file
   * @param beanclazz
   * @param titleExist
   * @return
   * @throws IOException
   */
  public List<ArrayList<String>> readXls(MultipartFile file){
    List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
    // IO流读取文件
    InputStream input = null;
    HSSFWorkbook wb = null;
    ArrayList<String> rowList = null;
    try {
      input =file.getInputStream() ;
      // 创建文档
      wb = new HSSFWorkbook(input);
      //读取sheet(页)
//      for(int numSheet=0;numSheet<wb.getNumberOfSheets();numSheet++){
      HSSFSheet hssfSheet = wb.getSheetAt(0);
//        if(hssfSheet == null){
//          continue;
//        }
      totalRows = hssfSheet.getLastRowNum();
      //读取Row,从第二行开始
      for(int rowNum = 5;rowNum <= totalRows;rowNum++){
        HSSFRow hssfRow = hssfSheet.getRow(rowNum);
        if(hssfRow!=null){
          rowList = new ArrayList<String>();
          totalCells = hssfRow.getLastCellNum();
          //读取列，从第一列开始
          for(int c=0;c<=totalCells+1;c++){
            HSSFCell cell = hssfRow.getCell(c);
            if(cell==null){
              rowList.add(ExcelUtil.EMPTY);
              continue;
            }
            rowList.add(ExcelUtil.getHValue(cell).trim());
          }
          list.add(rowList);
        }
      }
//      }
      return list;
    } catch (IOException e) {
      e.printStackTrace();
    } finally{
      try {
        input.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }
}
