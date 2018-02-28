package cn.pcorp.util;

import cn.pcorp.dao.BaseDao;
import cn.pcorp.model.DynaBean;



//import jxl.Workbook;
//import jxl.write.Label;
//import jxl.write.WritableSheet;
//import jxl.write.WritableWorkbook;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
//下面就是该文的主角ExcelUtil登场了，作为一个工具类，其内的所有方法都是静态的，方便使用。

//        ExcelUitl类

/**
 * @author     : HT
 * @Date       : 2017-5-18 下午8:13:21
 * @Comments   : 导入导出Excel工具类
 * @Version    : 1.0.0
 */

 public class ExcelUtil {

  /**
   * @param list      数据源
   * @param fieldMap  类的英文属性和Excel中的中文列名的对应关系
   *                  如果需要的是引用对象的属性，则英文属性使用类似于EL表达式的格式
   *                  如：list中存放的都是student，student中又有college属性，而我们需要学院名称，则可以这样写
   *                  fieldMap.put("college.collegeName","学院名称")
   * @param sheetName 工作表的名称
   * @param sheetSize 每个工作表中记录的最大个数
   * @param out       导出流
   * @throws ExcelException
   * @MethodName : listToExcel
   * @Description : 导出Excel（可以导出到本地文件系统，也可以导出到浏览器，可自定义工作表大小）
   */
  public static <T> void listToExcel(
    List<DynaBean> list,
    LinkedHashMap<String, String> fieldMap,
    String sheetName,
    int sheetSize,
    OutputStream out,
    DynaBean dynaBean
  ) throws ExcelException {


//    if (list.size() == 0 || list == null) {
//      throw new ExcelException("数据源中没有任何数据");
//    }

    if (sheetSize > 65535 || sheetSize < 1) {
      sheetSize = 65535;
    }

    //创建工作簿并发送到OutputStream指定的地方
    HSSFWorkbook wwb =new HSSFWorkbook();

    try {


      //因为2003的Excel一个工作表最多可以有65536条记录，除去列头剩下65535条
      //所以如果记录太多，需要放到多个工作表中，其实就是个分页的过程
      //1.计算一共有多少个工作表
      double sheetNum = Math.ceil(list.size() / new Integer(sheetSize).doubleValue());
      if (list.size()==0){
        sheetNum=1.0;
      }
      //2.创建相应的工作表，并向其中填充数据
      for (int i = 0; i < sheetNum; i++) {
        //如果只有一个工作表的情况
        if (0 >= sheetNum||sheetNum<=1) {
          HSSFSheet sheet = wwb.createSheet(sheetName);
          fillSheet(wwb,sheet, list, fieldMap, 0, list.size() - 1);

          //有多个工作表的情况
        } else {
          HSSFSheet sheet = wwb.createSheet(sheetName + (i + 1));

          //获取开始索引和结束索引
          int firstIndex = i * sheetSize;
          int lastIndex = (i + 1) * sheetSize - 1 > list.size() - 1 ? list.size() - 1 : (i + 1) * sheetSize - 1;
          //填充工作表
          fillSheet(wwb,sheet, list, fieldMap, firstIndex, lastIndex);
        }
      }

      wwb.write(out);
      out.close();

    } catch (Exception e) {
      e.printStackTrace();
      //如果是ExcelException，则直接抛出
      if (e instanceof ExcelException) {
        throw (ExcelException) e;

        //否则将其它异常包装成ExcelException再抛出
      } else {
        throw new ExcelException("导出Excel失败");
      }
    }

  }

  /**
   * @param list     数据源
   * @param fieldMap 类的英文属性和Excel中的中文列名的对应关系
   * @param out      导出流
   * @throws ExcelException
   * @MethodName : listToExcel
   * @Description : 导出Excel（可以导出到本地文件系统，也可以导出到浏览器，工作表大小为2003支持的最大值）
   */
  public static <T> void listToExcel(
    List<DynaBean> list,
    LinkedHashMap<String, String> fieldMap,
    String sheetName,
    OutputStream out,
    DynaBean dynaBean
  ) throws ExcelException {

    listToExcel(list, fieldMap, sheetName, 65535, out,dynaBean);

  }


  /**
   * @param list      数据源
   * @param fieldMap  类的英文属性和Excel中的中文列名的对应关系
   * @param sheetSize 每个工作表中记录的最大个数
   * @param response  使用response可以导出到浏览器
   * @throws ExcelException
   * @MethodName : listToExcel
   * @Description : 导出Excel（导出到浏览器，可以自定义工作表的大小）
   */
  public static <T> void listToExcel(
    List<DynaBean> list,
    LinkedHashMap<String, String> fieldMap,
    String sheetName,
    int sheetSize,
    HttpServletResponse response,
    DynaBean dynaBean
  ) throws ExcelException {

    //设置默认文件名为当前时间：年月日时分秒
    String fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()).toString();

    //设置response头信息
    response.reset();
    response.setContentType("application/vnd.ms-excel");        //改成输出excel文件
    response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");

    //创建工作簿并发送到浏览器
    try {

      OutputStream out = response.getOutputStream();
      listToExcel(list, fieldMap, sheetName, sheetSize, out,dynaBean);

    } catch (Exception e) {
      e.printStackTrace();

      //如果是ExcelException，则直接抛出
      if (e instanceof ExcelException) {
        throw (ExcelException) e;

        //否则将其它异常包装成ExcelException再抛出
      } else {
        throw new ExcelException("导出Excel失败");
      }
    }
  }


  /**
   * @param list     数据源
   * @param fieldMap 类的英文属性和Excel中的中文列名的对应关系
   * @param response 使用response可以导出到浏览器
   * @throws ExcelException
   * @MethodName : listToExcel
   * @Description : 导出Excel（导出到浏览器，工作表的大小是2003支持的最大值）
   */
  public static <T> void listToExcel(
    List<DynaBean> list,
    LinkedHashMap<String, String> fieldMap,
    String sheetName,
    HttpServletResponse response,
    DynaBean dynaBean
  ) throws ExcelException {

    listToExcel(list, fieldMap, sheetName, 65535, response,dynaBean);
  }

  /**
   * @param in           ：承载着Excel的输入流
   * @param sheetName   ：要导入的工作表序号
   * @param entityClass  ：List中对象的类型（Excel中的每一行都要转化为该类型的对象）
   * @param fieldMap     ：Excel中的中文列头和类的英文属性的对应关系Map
   * @param uniqueFields ：指定业务主键组合（即复合主键），这些列的组合不能重复
   * @return ：List
   * @throws ExcelException
   * @MethodName : excelToList
   * @Description : 将Excel转化为List
   */
  /*public static <T> List<T> excelToList(
    InputStream in,
    String sheetName,
    Class<T> entityClass,
    LinkedHashMap<String, String> fieldMap,
    String[] uniqueFields
  ) throws ExcelException {

    //定义要返回的list
    List<T> resultList = new ArrayList<T>();

    try {

      //根据Excel数据源创建WorkBook
      Workbook wb = Workbook.getWorkbook(in);
      //获取工作表
      Sheet sheet = wb.getSheet(sheetName);

      //获取工作表的有效行数
      int realRows = 0;
      for (int i = 0; i < sheet.getRows(); i++) {

        int nullCols = 0;
        for (int j = 0; j < sheet.getColumns(); j++) {
          Cell currentCell = sheet.getCell(j, i);
          if (currentCell == null || "".equals(currentCell.getContents().toString())) {
            nullCols++;
          }
        }

        if (nullCols == sheet.getColumns()) {
          break;
        } else {
          realRows++;
        }
      }


      //如果Excel中没有数据则提示错误
      if (realRows <= 1) {
        throw new ExcelException("Excel文件中没有任何数据");
      }


      Cell[] firstRow = sheet.getRow(0);

      String[] excelFieldNames = new String[firstRow.length];

      //获取Excel中的列名
      for (int i = 0; i < firstRow.length; i++) {
        excelFieldNames[i] = firstRow[i].getContents().toString().trim();
      }

      //判断需要的字段在Excel中是否都存在
      boolean isExist = true;
      List<String> excelFieldList = Arrays.asList(excelFieldNames);
      for (String cnName : fieldMap.keySet()) {
        if (!excelFieldList.contains(cnName)) {
          isExist = false;
          break;
        }
      }

      //如果有列名不存在，则抛出异常，提示错误
      if (!isExist) {
        throw new ExcelException("Excel中缺少必要的字段，或字段名称有误");
      }


      //将列名和列号放入Map中,这样通过列名就可以拿到列号
      LinkedHashMap<String, Integer> colMap = new LinkedHashMap<String, Integer>();
      for (int i = 0; i < excelFieldNames.length; i++) {
        colMap.put(excelFieldNames[i], firstRow[i].getColumn());
      }


      //判断是否有重复行
      //1.获取uniqueFields指定的列
      Cell[][] uniqueCells = new Cell[uniqueFields.length][];
      for (int i = 0; i < uniqueFields.length; i++) {
        int col = colMap.get(uniqueFields[i]);
        uniqueCells[i] = sheet.getColumn(col);
      }

      //2.从指定列中寻找重复行
      for (int i = 1; i < realRows; i++) {
        int nullCols = 0;
        for (int j = 0; j < uniqueFields.length; j++) {
          String currentContent = uniqueCells[j][i].getContents();
          Cell sameCell = sheet.findCell(currentContent,
            uniqueCells[j][i].getColumn(),
            uniqueCells[j][i].getRow() + 1,
            uniqueCells[j][i].getColumn(),
            uniqueCells[j][realRows - 1].getRow(),
            true);
          if (sameCell != null) {
            nullCols++;
          }
        }

        if (nullCols == uniqueFields.length) {
          throw new ExcelException("Excel中有重复行，请检查");
        }
      }

      //将sheet转换为list
      for (int i = 1; i < realRows; i++) {
        //新建要转换的对象
        T entity = entityClass.newInstance();

        //给对象中的字段赋值
        for (Entry<String, String> entry : fieldMap.entrySet()) {
          //获取中文字段名
          String cnNormalName = entry.getKey();
          //获取英文字段名
          String enNormalName = entry.getValue();
          //根据中文字段名获取列号
          int col = colMap.get(cnNormalName);

          //获取当前单元格中的内容
          String content = sheet.getCell(col, i).getContents().toString().trim();

          //给对象赋值
          setFieldValueByName(enNormalName, content, entity);
        }

        resultList.add(entity);
      }
    } catch (Exception e) {
      e.printStackTrace();
      //如果是ExcelException，则直接抛出
      if (e instanceof ExcelException) {
        throw (ExcelException) e;

        //否则将其它异常包装成ExcelException再抛出
      } else {
        e.printStackTrace();
        throw new ExcelException("导入Excel失败");
      }
    }
    return resultList;
  }

*/

  /**
   * @param file  承载着Excel的文件
   * @param basedao：
   * @param dynaBean2： d当前登录用户的单位信息
   * @param excelName   ：要导入的表名字
   * @param tyMaps  ：字段对应的数据类型
   * @param fieldMap     ：Excel中的中文列头和类的英文属性的对应关系Map
   * @param uniqueFields ：指定业务主键组合（即复合主键），这些列的组合不能重复
   * @return ：List
   * @throws ExcelException
   * @MethodName : excelToList
   * @Description : 将Excel转化为List
   */
  public static <T> List<DynaBean> excelToList(
    DynaBean dynaBean2,
    BaseDao basedao,
    MultipartFile file,
          String excelName,
          Map  tyMaps,
          LinkedHashMap<String, String> fieldMap,
          String[] uniqueFields
  ) throws ExcelException {

    String companyCode="";
    Map map = (Map)dynaBean2.get("USERINFO");
    if (map!=null) {
       companyCode = map.get("COMPANYCODE").toString();
    }
    //定义要返回的list
    List<DynaBean> resultList = new ArrayList<>();
    Workbook wb = null;
    try {
      String prefix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
      InputStream in = file.getInputStream();
      if(!ExcelUtil.EMPTY.equals(prefix)){
        if(ExcelUtil.OFFICE_EXCEL_2003_POSTFIX.equals(prefix)){
          wb = new HSSFWorkbook(in);
        }else if(ExcelUtil.OFFICE_EXCEL_2010_POSTFIX.equals(prefix)){
          wb = new  XSSFWorkbook(in);
        }else{
          return null;
        }
      }
      //根据Excel数据源创建WorkBook

      //获取工作表
      Sheet sheet = wb.getSheetAt(0);

  /**    //获取工作表的有效行数
      int realRows = 0;
      int rowNums = sheet.getLastRowNum();
      for (int i = 0; i < rowNums; i++) {

        int nullCols = 0;
        int columns = sheet.getRow(0).getLastCellNum();
        for (int j = 0; j <columns; j++) {
         Row row =  sheet.getRow(i);
          String  currentCell = row.getCell(j).getStringCellValue();
          if (currentCell == null || "".equals(currentCell)) {
            nullCols++;
          }
        }

        if (nullCols == sheet.getRow(i).getPhysicalNumberOfCells()) {
          continue;
        } else {
          realRows++;
        }*/



//      //如果Excel中没有数据则提示错误
//      if (realRows <= 1) {
//        throw new ExcelException("Excel文件中没有任何数据");
//      }



        Cell cell = null;

      //获取Excel中的列名
      LinkedHashMap<String, Integer> colMap = new LinkedHashMap<String, Integer>();
      Row  xssfRow1 = sheet.getRow(0);
      String[] excelFieldNames = new String[xssfRow1.getLastCellNum()+1];
      if(xssfRow1!=null) {
        int totalCells = xssfRow1.getLastCellNum();
        //读取列，从第一列开始
        for (int c = 0; c <= totalCells; c++) {
          Cell cells = xssfRow1.getCell(c);
          if (cells == null) {
            excelFieldNames[c] = ExcelUtil.EMPTY;
            continue;
          }
          excelFieldNames[c] = cells.getStringCellValue().trim();
          colMap.put(cells.getStringCellValue(), cells.getColumnIndex());
        }
      }

      //判断需要的字段在Excel中是否都存在
      boolean isExist = true;
      List<String> excelFieldList = Arrays.asList(excelFieldNames);
      for (String cnName : fieldMap.keySet()) {
        if (!excelFieldList.contains(cnName)) {
          isExist = false;
          break;
        }
      }




      //将列名和列号放入Map中,这样通过列名就可以拿到列号
//      LinkedHashMap<String, Integer> colMap = new LinkedHashMap<String, Integer>();
//      for (int i = 0; i < excelFieldNames.length; i++) {
//        Row xssfRow = sheet.getRow(0);
//        if (xssfRow != null) {
//          for (int j = 0; j <xssfRow.getLastCellNum(); j++) {
//              cell = xssfRow.getCell(i);
//            if(cell!=null){
//              colMap.put(excelFieldNames[i], cell.getColumnIndex());
//            }
//          }
//        }
//      }


      //判断是否有重复行
      //1.获取uniqueFields指定的列
//      Cell[][] uniqueCells = new Cell[uniqueFields.length][];
//      for (int i = 0; i < uniqueFields.length; i++) {
//        int col = colMap.get(uniqueFields[i]);
//        uniqueCells[i] = sheet.getColumn(col);
//      }

      //2.从指定列中寻找重复行
//      for (int i = 1; i < realRows; i++) {
//        int nullCols = 0;
//        for (int j = 0; j < uniqueFields.length; j++) {
//          String currentContent = uniqueCells[j][i].getContents();
//          Cell sameCell = sheet.findCell(currentContent,
//                  uniqueCells[j][i].getColumn(),
//                  uniqueCells[j][i].getRow() + 1,
//                  uniqueCells[j][i].getColumn(),
//                  uniqueCells[j][realRows - 1].getRow(),
//                  true);
//          if (sameCell != null) {
//            nullCols++;
//          }
//        }
//
////
//      }
      DynaBean  dynaBean =null;

      //将sheet转换为list
     int realRows = sheet.getLastRowNum();
      for (int i = 1; i < realRows; i++) {
        //新建要转换的对象
//        T entity = entityClass.newInstance();
           dynaBean= new DynaBean();
        //给对象中的字段赋值
        DynaBean  dynaBean1 =  new DynaBean();
        for (Entry<String, String> entry : fieldMap.entrySet()) {
          //获取中文字段名
          String cnNormalName = entry.getKey();
          //获取英文字段名
          String enNormalName = entry.getValue();
          for (int j = 0; j < excelFieldNames.length; j++) {
            String excelFieldName = excelFieldNames[j];
            if (!excelFieldName.equals(cnNormalName)) {
              continue;
            }
            //.out.print("123");
          //根据中文字段名获取列号
            int col = colMap.get(cnNormalName);
            if (col==30){
              //.out.print(cnNormalName);
            }

            dynaBean.set("TABLE_CODE",excelName);
          //获取当前单元格中的内容
//          String content = sheet.getCellComment(col, i).getString().toString().trim();
          if (enNormalName.equals("ID")){
            continue;
          }

          String  typeName = tyMaps.get(enNormalName).toString();
          String content = getValue(sheet,i,col,typeName);

          if(content.equals("")||content ==null){
            continue;
          }
          if((enNormalName.trim()).equals("")||enNormalName==null){
            continue;
          }

//          if(enNormalName.equals("SCOMPANY_CODE")) {
//            dynaBean.set(enNormalName, companyCode);
//            continue;
//          }
            //给对象赋值
//          SimpleDateFormat  simpleDateFormat =  new SimpleDateFormat("yyyy年mm月dd日");

            if (enNormalName.equals("SCUSTOMER_NAME")){
              dynaBean1.set("SCUSTOMER_NAME",content);
            }
            if (enNormalName.equals("CUSTOMER_TYPE")){
              dynaBean1.set("SCUSTOMER_CLASSIFY",content);
            }
            if (enNormalName.equals("SBUREAU_BUREAU")){
              dynaBean1.set("SBUREAU",content);
            }

            if (enNormalName.equals("SSTART_DATE")){
              if (content!=null){
                String  contents = getDateValue(content);
                dynaBean.set(enNormalName, contents);
                continue;
              }
              dynaBean.set(enNormalName, content);
              continue;
            }else if (enNormalName.equals("SEND_DATE")){
              if (content!=null){
                String  contents = getDateValue(content);
                dynaBean.set(enNormalName, contents);
                continue;
              }
              dynaBean.set(enNormalName, content);
              continue;
            }else if (enNormalName.equals("SESTIMATED_DATE")){
              if (content!=null){
                String  contents = getDateValue(content);
                dynaBean.set(enNormalName, contents);
                continue;
              }
              dynaBean.set(enNormalName, content);
              continue;
            }else if (enNormalName.equals("SSIGN_DATE")){
              if (content!=null){
                String  contents = getDateValue(content);
                dynaBean.set(enNormalName, contents);
                continue;
              }
              dynaBean.set(enNormalName, content);
              continue;
            }
            dynaBean.set(enNormalName, content);

//          setFieldValueByName(enNormalName, content, entity);
          }

        }
        if (dynaBean1.get("SCUSTOMER_NAME")!=null){
          String eq =  dynaBean1.get("SCUSTOMER_NAME").toString();
          if (excelName.equals("AR_ACCOUNT")) {
            if (eq != null || !eq.equals("")) {
              dynaBean1.set("TABLE_CODE", "AR_CUSTOMER");
              List<DynaBean> list = basedao.findWithQueryNoCache(new DynaBean("AR_CUSTOMER",
                "and SCUSTOMER_NAME =" +"'"+dynaBean1.getStr("SCUSTOMER_NAME")+"'",""));
              if (list.size()==0){
                dynaBean1.set("SCUSTOMER_CODE",UUID.randomUUID().toString());
                basedao.insertOne(dynaBean1);
              }
            }
          }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd");
      if (dynaBean2.getStr("SPERIODCODE")!=null){  Date date =  simpleDateFormat.parse(dynaBean2.getStr("SPERIODCODE"));
        Long  datTime = date.getTime();
//        int  a = datTime.intValue();
        if (dynaBean.getStr("SSTART_DATE")!=null){
          if (!dynaBean.getStr("SSTART_DATE").equals("0")){

            String  ss =  dynaBean.getStr("SSTART_DATE");
            Long longw = Long.valueOf(ss)*1000;
//            Long l = (datTime-longw)/(1000*3600/24);
            SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM");
           String  s =  sdf.format(longw);
           Date  date1 = sdf.parse(s);

            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(sdf.format(date)));
            int year1 = c.get(Calendar.YEAR);
            int month1 = c.get(Calendar.MONTH);

            c.setTime(sdf.parse(sdf.format(date1)));
            int year2 = c.get(Calendar.YEAR);
            int month2 = c.get(Calendar.MONTH);

            int result;
            if(year1 == year2) {
              result = month1 - month2;
            } else {
              result = 12*(year1 - year2) + month1 - month2;
            }
            //.out.println(result);


//         int  b = Integer.parseInt(dynaBean.getStr("SSTART_DATE").trim());
//         int sum = a-b;
//            Date  date1 = new Date();
//            date1.setTime(l);
//            String s = Long.toString(l);   // simpleDateFormat2.format(date1).trim();
            dynaBean.set("NACC_AGING",result);
          }else {
            dynaBean.set("NACC_AGING",0);
          }
        }
//        if (excelName.equals("AR_ACCOUNT")) {
//          basedao.delete(new DynaBean("AR_ACCOUNT",
//            "and SPERIODCODE =" + "'" +dynaBean1.getStr("SPERIODCODE")+ "and SPERIODCODE ="+"'"+"'"+dynaBean1.getStr("SPERIODCODE")+"'", ""));
//        }
//        if (){
//
//        }

        dynaBean.set("SPERIODCODE",dynaBean2.getStr("SPERIODCODE"));
        basedao.insertOne(dynaBean);
        resultList.add(dynaBean);
        }
      }


    } catch (Exception e) {
      e.printStackTrace();
      //如果是ExcelException，则直接抛出
      if (e instanceof ExcelException) {
        throw (ExcelException) e;

        //否则将其它异常包装成ExcelException再抛出
      } else {
        e.printStackTrace();
        throw new ExcelException("导入Excel失败");
      }
    }
    return resultList;
  }
    private static String  getDateValue(String content) throws ParseException {
        try {
          SimpleDateFormat  simpleDateFormat =  new SimpleDateFormat("yyyy年MM月dd日");
          Date date = simpleDateFormat.parse(content.trim());
          String contents = String.valueOf(date.getTime()/1000);
          return  contents;
        }catch (Exception e){
          return "";
        }
    }

      public static  String  getValue(Sheet  sheet,int i,int col,String typeName){
        String content="";
        try{

         Cell cell =  sheet.getRow(i).getCell(col);
         short s = cell.getCellStyle().getDataFormat();
         //.out.println("数据下表+++++++++++++++++++++++++++++++"+s+"++++++++++++++++++++++++++++++++++++++++++++++++++++++");
         if (s==31||s==57){
             Date date = cell.getDateCellValue();
           SimpleDateFormat  sim = new SimpleDateFormat("yyyy年MM月dd日");
           content = sim.format(date);
           //.out.print("shuju"+content+"+++++++++++++++++++++++++++++++++++++++++++++++");
         }else{
          sheet.getRow(i).getCell(col).setCellType(CellType.STRING);
          content =sheet.getRow(i).getCell(col).getStringCellValue();
        }

        }catch (Exception e){
        e.getMessage();
        if (typeName.equals("num")){
          return "";
        }else {
          return "";
        }

        }
        if (content.equals("")){
          if (typeName.equals("num")){
            return "";
          }else {
            return "";
          }
        }
        return  content;
      }
        /*<-------------------------辅助的私有方法----------------------------------------------->*/

  /**
   * @param fieldName 字段名
   * @param o         对象
   * @return 字段值
   * @MethodName : getFieldValueByName
   * @Description : 根据字段名获取字段值
   */
  private static Object getFieldValueByName(String fieldName, Object o,DynaBean dynaBean) throws Exception {

    Object value = null;
    Field field = getFieldByName(fieldName, o.getClass());

    List<Map> list = (List<Map>) dynaBean.get("P_APPFIELDS");
    if (field != null) {
      field.setAccessible(true);
      value = field.get(o);
    } else {
      throw new ExcelException(o.getClass().getSimpleName() + "类不存在字段名 " + fieldName);
    }

    return value;
  }

  /**
   * @param fieldName 字段名
   * @param clazz     包含该字段的类
   * @return 字段
   * @MethodName : getFieldByName
   * @Description : 根据字段名获取字段
   */
  private static Field getFieldByName(String fieldName, Class<?> clazz) {
    //拿到本类的所有字段
    Field[] selfFields = clazz.getDeclaredFields();

    //如果本类中存在该字段，则返回
    for (Field field : selfFields) {
      if (field.getName().equals(fieldName)) {
        return field;
      }
    }

    //否则，查看父类中是否存在此字段，如果有则返回
    Class<?> superClazz = clazz.getSuperclass();
    if (superClazz != null && superClazz != Object.class) {
      return getFieldByName(fieldName, superClazz);
    }

    //如果本类和父类都没有，则返回空
    return null;
  }


  /**
   * @param fieldNameSequence 带路径的属性名或简单属性名
   * @param o                 对象
   * @return 属性值
   * @throws Exception
   * @MethodName : getFieldValueByNameSequence
   * @Description :
   * 根据带路径或不带路径的属性名获取属性值
   * 即接受简单属性名，如userName等，又接受带路径的属性名，如student.department.name等
   */
  private static Object getFieldValueByNameSequence(String fieldNameSequence, Object o,DynaBean dynaBean) throws Exception {

    Object value = null;

    //将fieldNameSequence进行拆分
    String[] attributes = fieldNameSequence.split("\\.");
    if (attributes.length == 1) {
      value = dynaBean.get(fieldNameSequence);
//      value = getFieldValueByName(fieldNameSequence, o,dynaBean);
    } else {
      //根据属性名获取属性对象
      Object fieldObj = getFieldValueByName(attributes[0], o,dynaBean);
      String subFieldNameSequence = fieldNameSequence.substring(fieldNameSequence.indexOf(".") + 1);
      value = getFieldValueByNameSequence(subFieldNameSequence, fieldObj,dynaBean);
    }
    return value;

  }


  /**
   * @param fieldName  字段名
   * @param fieldValue 字段值
   * @param o          对象
   * @MethodName : setFieldValueByName
   * @Description : 根据字段名给对象的字段赋值
   */
  private static void setFieldValueByName(String fieldName, Object fieldValue, Object o) throws Exception {

    Field field = getFieldByName(fieldName, o.getClass());
    if (field != null) {
      field.setAccessible(true);
      //获取字段类型
      Class<?> fieldType = field.getType();

      //根据字段类型给字段赋值
      if (String.class == fieldType) {
        field.set(o, String.valueOf(fieldValue));
      } else if ((Integer.TYPE == fieldType)
        || (Integer.class == fieldType)) {
        field.set(o, Integer.parseInt(fieldValue.toString()));
      } else if ((Long.TYPE == fieldType)
        || (Long.class == fieldType)) {
        field.set(o, Long.valueOf(fieldValue.toString()));
      } else if ((Float.TYPE == fieldType)
        || (Float.class == fieldType)) {
        field.set(o, Float.valueOf(fieldValue.toString()));
      } else if ((Short.TYPE == fieldType)
        || (Short.class == fieldType)) {
        field.set(o, Short.valueOf(fieldValue.toString()));
      } else if ((Double.TYPE == fieldType)
        || (Double.class == fieldType)) {
        field.set(o, Double.valueOf(fieldValue.toString()));
      } else if (Character.TYPE == fieldType) {
        if ((fieldValue != null) && (fieldValue.toString().length() > 0)) {
          field.set(o, Character
            .valueOf(fieldValue.toString().charAt(0)));
        }
      } else if (Date.class == fieldType) {
        field.set(o, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fieldValue.toString()));
      } else {
        field.set(o, fieldValue);
      }
    } else {
      throw new ExcelException(o.getClass().getSimpleName() + "类不存在字段名 " + fieldName);
    }
  }


  /**
   * @param ws
   * @MethodName : setColumnAutoSize
   * @Description : 设置工作表自动列宽和首行加粗
   */
//  private static void setColumnAutoSize(Sheet ws, int extraWith) {
//    //获取本列的最宽单元格的宽度
//    for (int i = 0; i < ws.; i++) {
//      int colWith = 0;
//      for (int j = 0; j < ws.get; j++) {
//        String content = ws.getCellComment(i, j).getAuthor().toString();
//        int cellWith = content.length();
//        if (colWith < cellWith) {
//          colWith = cellWith;
//        }
//      }
//      //设置单元格的宽度为最宽宽度+额外宽度
//      ws.setColumnWidth(i, colWith + extraWith);
//    }

//  }

  /**
   * @param sheet      工作表
   * @param list       数据源
   * @param fieldMap   中英文字段对应关系的Map
   * @param firstIndex 开始索引
   * @param lastIndex  结束索引
   * @MethodName : fillSheet
   * @Description : 向工作表中填充数据
   */
  private static <T> void fillSheet(
    HSSFWorkbook wb,
    HSSFSheet sheet,
    List<DynaBean> list,
    LinkedHashMap<String, String> fieldMap,
    int firstIndex,
    int lastIndex
  ) throws Exception {

    //定义存放英文字段名和中文字段名的数组
    String[] enFields = new String[fieldMap.size()];
    String[] cnFields = new String[fieldMap.size()];
    HSSFCellStyle style = wb.createCellStyle();
    //填充数组
    int count = 0;
    for (Entry<String, String> entry : fieldMap.entrySet()) {
      enFields[count] = entry.getKey();
      cnFields[count] = entry.getValue();
      count++;
    }
    HSSFRow row =  sheet.createRow((int) 0);
    //填充表头
    for (int i = 0; i < cnFields.length; i++) {
      HSSFCell cell = row.createCell(i);
      cell.setCellValue(cnFields[i]);
      cell.setCellStyle(style);
      sheet.autoSizeColumn(i);
//      sheet.SetColumnWidth(i, (00 * 256);
//      Label label = new Label(i, 0, cnFields[i]);
//      sheet.addCell(label);
    }

    //填充内容
    int rowNo = 1;
    int size = 0;
    if(list.size()>=1) {
      for (int index = firstIndex; index <= lastIndex; index++) {
        //获取单个对象
        DynaBean dynaBean = list.get(index);
        for (int i = 0; i < enFields.length; i++) {
          Object objValue = getFieldValueByNameSequence(enFields[i], dynaBean, dynaBean);
          String fieldValue = objValue == null ? "" : objValue.toString();
        if (rowNo!=size) {
          size=rowNo;
          row = sheet.createRow(rowNo);
        }
          row.createCell(i).setCellValue(fieldValue);
//          Label label = new Label(i, rowNo, fieldValue);
//          sheet.addCell(label);
        }
        rowNo++;
      }
    }
    //设置自动列宽
//    setColumnAutoSize(sheet, 5);
  }


/*-------------------------------End--------------------------------------*/










//
//  String[] excelHeader = { "Sno", "Name", "Age"};
//  public HSSFWorkbook export(List<Student> list) {
//    HSSFWorkbook wb = new HSSFWorkbook();
//    HSSFSheet sheet = wb.createSheet("Student");
//    HSSFRow row = sheet.createRow((int) 0);
//    HSSFCellStyle style = wb.createCellStyle();
//    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//
//    for (int i = 0; i < excelHeader.length; i++) {
//      HSSFCell cell = row.createCell(i);
//      cell.setCellValue(excelHeader[i]);
//      cell.setCellStyle(style);
//      sheet.autoSizeColumn(i);
//      // sheet.SetColumnWidth(i, 100 * 256);
//    }
//
//    for (int i = 0; i < list.size(); i++) {
//      row = sheet.createRow(i + 1);
//      Student student = list.get(i);
//      row.createCell(0).setCellValue(student.getSno());
//      row.createCell(1).setCellValue(student.getName());
//      row.createCell(2).setCellValue(student.getAge());
//    }
//    return wb;
//  }
//

















  /**
   * Excel工具类
   * @author lp
   *
   */
    public static final String OFFICE_EXCEL_2003_POSTFIX = "xls";
    public static final String OFFICE_EXCEL_2010_POSTFIX = "xlsx";
    public static final String EMPTY = "";
    public static final String POINT = ".";
    public static SimpleDateFormat sdf =   new SimpleDateFormat("yyyy/MM/dd");
    /**
     * 获得path的后缀名
     * @param path
     * @return
     */
    public static String getPostfix(String path){
      if(path==null || EMPTY.equals(path.trim())){
        return EMPTY;
      }
      if(path.contains(POINT)){
        return path.substring(path.lastIndexOf(POINT)+1,path.length());
      }
      return EMPTY;
    }
    /**
     * 单元格格式
     * @param hssfCell
     * @return
     */
    @SuppressWarnings({ "static-access", "deprecation" })
    public static String getHValue(Cell hssfCell){
      try {
      if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
        return String.valueOf(hssfCell.getBooleanCellValue());
      } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
        String cellValue = "";
        if(HSSFDateUtil.isCellDateFormatted(hssfCell)){
          Date date = HSSFDateUtil.getJavaDate(hssfCell.getNumericCellValue());
          cellValue = sdf.format(date);
        }else{
          DecimalFormat df = new DecimalFormat("#.##");
          cellValue = df.format(hssfCell.getNumericCellValue());
          String strArr = cellValue.substring(cellValue.lastIndexOf(POINT)+1,cellValue.length());
          if(strArr.equals("00")){
              cellValue = cellValue.substring(0, cellValue.lastIndexOf(POINT));
          }
        }
        return cellValue;
      } else {
        return String.valueOf(hssfCell.getStringCellValue().toString());
      }
      }catch (Exception e){
        return  "0";
      }
    }
    /**
     * 单元格格式
     * @param xssfCell
     * @return
     */
    public static String getXValue(Cell xssfCell){
      if (xssfCell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
        return String.valueOf(xssfCell.getBooleanCellValue());
      } else if (xssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
        String cellValue = "";
        if(XSSFDateUtil.isCellDateFormatted(xssfCell)){
          Date date = XSSFDateUtil.getJavaDate(xssfCell.getNumericCellValue());
          cellValue = sdf.format(date);
        }else{
          DecimalFormat df = new DecimalFormat("#.##");
          cellValue = df.format(xssfCell.getNumericCellValue());
          String strArr = cellValue.substring(cellValue.lastIndexOf(POINT)+1,cellValue.length());
          if(strArr.equals("00")){
            cellValue = cellValue.substring(0, cellValue.lastIndexOf(POINT));
          }
        }
        return cellValue;
      } else {
        return String.valueOf(xssfCell.getStringCellValue());
      }
    }
//    /**
//     * 自定义xssf日期工具类
//     * @author lp
//     *
//     */




  }
